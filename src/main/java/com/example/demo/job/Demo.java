package com.example.demo.job;

import com.example.demo.storage.ConcurrencyStorage;
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@RequiredArgsConstructor
public class Demo {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final ConcurrencyStorage<Integer> concurrencyStorage;

    private static final int chunkSize = 3;
    private static final int itemSize = 10;

    @Bean
    public Job demoJob() {
        return jobBuilderFactory.get("demo-job")
                .start(demoStep())
                .next(demoStep2())
                .build();
    }

    @Bean
    @JobScope
    public Step demoStep() {
        return stepBuilderFactory.get("demo-step")
                .<Integer, Integer>chunk(chunkSize)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .faultTolerant()
                .skip(RuntimeException.class)
                .skipLimit(Integer.MAX_VALUE)
                .build();
    }

    @Bean
    @JobScope
    public Step demoStep2(){
        return stepBuilderFactory.get("demo-step2")
                .<Integer, Integer>chunk(chunkSize)
                .reader(reader2())
                .processor(processor2())
                .writer(writer2())
                .build();
    }

    @Bean
    @StepScope
    public ListItemReader<Integer> reader() {
        List<Integer> items = Stream.iterate(1, i -> i + 1).limit(itemSize).collect(Collectors.toList());
        items.forEach(it -> System.out.println("reader : item = "+it));

        return new ListItemReader<>(items);
    }

    @Bean
    @StepScope
    public ItemProcessor<Integer, Integer> processor() {
        return item ->{
            System.out.println("processor : item = "+item);
            return item;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<Integer> writer() {
        return items -> items.forEach(item -> {
            if (item%3==0)
                throw new RuntimeException("hi ?");

            this.concurrencyStorage.put(String.valueOf(item), item);

            System.out.println("writer : item = "+item);
        });
    }

    @Bean
    @StepScope
    public ListItemReader<Integer> reader2() {
        List<Integer> items = this.concurrencyStorage.getAllValue();
        items.forEach(it -> System.out.println("reader2 : item = "+it));

        return new ListItemReader<>(items);
    }

    @Bean
    @StepScope
    public ItemProcessor<Integer, Integer> processor2() {
        return item ->{
            System.out.println("processor2 : item = "+item);
            return item;
        };
    }

    @Bean
    @StepScope
    public ItemWriter<Integer> writer2() {
        return items -> items.forEach(item -> {
            System.out.println("writer2 : " + item);
        });
    }
}
