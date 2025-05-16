package pl.kkozera.recruitment_task.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.kkozera.recruitment_task.configuration.annotation.AllowedSortFields;
import pl.kkozera.recruitment_task.exception.InvalidPageRequestException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class PageableSanitizerAdvice implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SanitizingPageableResolver());
    }

    private static class SanitizingPageableResolver implements HandlerMethodArgumentResolver {

        private final PageableHandlerMethodArgumentResolver defaultResolver = new PageableHandlerMethodArgumentResolver();

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return Pageable.class.equals(parameter.getParameterType());
        }

        @Override
        public Object resolveArgument(
                MethodParameter parameter,
                ModelAndViewContainer mavContainer,
                NativeWebRequest webRequest,
                WebDataBinderFactory binderFactory) {

            Pageable pageable = defaultResolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

            int page = pageable.getPageNumber();
            int size = pageable.getPageSize();

            if (page < 0 || size < 1) {
                throw new InvalidPageRequestException("Page index must be >= 0 and size must be >= 1.");
            }

            Object handler = webRequest.getAttribute("org.springframework.web.servlet.HandlerMapping.bestMatchingHandler", NativeWebRequest.SCOPE_REQUEST);
            AllowedSortFields annotation = null;
            if (handler instanceof HandlerMethod handlerMethod) {
                annotation = handlerMethod.getMethodAnnotation(AllowedSortFields.class);
            }

            List<String> allowedSortFields = annotation != null
                    ? Arrays.asList(annotation.value())
                    : Collections.singletonList("id");

            List<Sort.Order> validatedOrders = pageable.getSort().stream()
                    .peek(order -> {
                        if (!allowedSortFields.contains(order.getProperty())) {
                            throw new InvalidPageRequestException("Invalid sort field: " + order.getProperty());
                        }
                    })
                    .collect(Collectors.toList());

            return PageRequest.of(page, size, Sort.by(validatedOrders));
        }
    }
}