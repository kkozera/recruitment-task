package pl.kkozera.recruitment_task;

import org.springframework.boot.SpringApplication;

public class TestRecruitmentTaskApplication {

	public static void main(String[] args) {
		SpringApplication.from(RecruitmentTaskApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
