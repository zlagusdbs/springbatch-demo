package com.example.demo.job;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
public class Demo {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job demoJob() {
        return jobBuilderFactory.get("demo-job")
                .start(demoStep())
                .build();
    }

    @Bean
    @JobScope
    public Step demoStep() {
        return stepBuilderFactory.get("demo-step")
                .<Integer, Integer>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    @StepScope
    public ListItemReader<Integer> reader() {
        return new ListItemReader<>(Stream.iterate(1, i -> i + 1).limit(10).collect(Collectors.toList()));
    }

    @Bean
    @StepScope
    public ItemProcessor<Integer, Integer> processor() {
        return item -> item;
    }

    @Bean
    @StepScope
    public ItemWriter<Integer> writer() {
        return items -> items.forEach(it -> {
            System.out.println("writer : "+it);
        });
    }
}
