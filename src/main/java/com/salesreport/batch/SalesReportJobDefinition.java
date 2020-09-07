package com.salesreport.batch;

import com.salesreport.converter.TransformedInputData;
import com.salesreport.repository.CustomerRepository;
import com.salesreport.repository.OutputDataRepository;
import com.salesreport.repository.SalesmanRepository;
import com.salesreport.utils.Messages;
import com.salesreport.config.ConfigBatchProperties;
import com.salesreport.converter.InputData;
import com.salesreport.factory.InputDataFactory;
import com.salesreport.model.Sales;
import com.salesreport.repository.SalesRepository;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Map;

@Configuration
public class SalesReportJobDefinition {

    private static final String JOB_SALES_REPORT = "job-sales-report";
    private static final String JOB_STEP_TRANSFORM_SALES_REPORT = "transform-sales-report";
    private static final String JOB_STEP_CREATE_SALES_REPORT = "create-sales-report";
    private static final String JOB_STEP_RESET_SALES_REPORT = "reset-data-sales-report";
    private static final String JOB_STEP_ERROR_SALES_REPORT = "error-sales-report";

    @Autowired
    private Messages messages;
    @Autowired
    private ConfigBatchProperties jobProperties;
    @Autowired
    private OutputDataRepository outputDataRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private SalesmanRepository salesmanRepository;
    @Autowired
    private SalesRepository salesRepository;

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory,
                   StepBuilderFactory stepBuilderFactory,
                   ItemReader<InputData> itemReader,
                   ItemProcessor<InputData, TransformedInputData> itemProcessor,
                   ItemWriter<TransformedInputData> itemWriter) {

        final ConfigBatchProperties.JobProperties jobProps = jobProperties.getSalesReport();

        final Step transform =
                stepBuilderFactory
                        .get(JOB_STEP_TRANSFORM_SALES_REPORT)
                        .<InputData, TransformedInputData>chunk(jobProps.getChunkSize().intValue())
                        .reader(itemReader)
                        .processor(itemProcessor)
                        .writer(itemWriter)
                        .build();

        final Step report =
                stepBuilderFactory
                        .get(JOB_STEP_CREATE_SALES_REPORT)
                        .tasklet(report())
                        .build();

        final Step reset =
                stepBuilderFactory
                        .get(JOB_STEP_RESET_SALES_REPORT)
                        .tasklet(reset())
                        .build();

        final Step error =
                stepBuilderFactory
                        .get(JOB_STEP_ERROR_SALES_REPORT)
                        .tasklet(error())
                        .build();

        return jobBuilderFactory.get(JOB_SALES_REPORT)
                .incrementer(new RunIdIncrementer())
                .start(transform).on(BatchStatus.COMPLETED.name()).to(report)
                .from(report).on(BatchStatus.COMPLETED.name()).to(reset)
                .from(transform).on(BatchStatus.FAILED.name()).to(error).on("*").fail()
                .from(report).on(BatchStatus.FAILED.name()).to(error).on("*").fail()
                .end()
                .build();
    }

    /**
     * The reader step job: read the file as a raw input data.
     *
     * @param fileAbsolutePath the file to read
     * @return reader step job.
     */
    @Bean
    @StepScope
    public FlatFileItemReader<InputData> itemReader(@Value("#{jobParameters[fileAbsolutePath]}") String fileAbsolutePath) {
        final DefaultLineMapper<InputData> defaultLineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        lineTokenizer.setNames("raw");
        lineTokenizer.setDelimiter("#$#");
        lineTokenizer.setStrict(false);

        final BeanWrapperFieldSetMapper<InputData> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(InputData.class);
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);

        return new FlatFileItemReaderBuilder<InputData>()
                .name("DAT-Reader")
                .resource(new FileSystemResource(fileAbsolutePath))
                .lineMapper(defaultLineMapper)
                .build();
    }

    /**
     * The processor step job: transform the raw input data in the mapped java object.
     *
     * @return processor step job.
     */
    @Bean
    public ItemProcessor<InputData, TransformedInputData> itemProcessor() {
        return InputDataFactory::create;
    }

    /**
     * The writer step job: save the preprocessed data in the db.
     *
     * @return writer step job.
     */
    @Bean
    public ItemWriter<TransformedInputData> itemWriter() {
        return items -> outputDataRepository.saveAll(items);
    }

    /**
     * The task to create a report with a preprocessed data.
     *
     * @return tasklet report creation.
     */
    @Bean
    public Tasklet report() {
        return (contribution, chunkContext) -> {
            final Map<String, Object> parameters = chunkContext.getStepContext().getJobParameters();
            final String outputFileAbsolutePath = (String) parameters.get(JobConstants.OUTPUT_FILE_ABSOLUTE_PATH_SUCCESS_PARAM);

            final long totalCustomers = customerRepository.totalUniqueCustomers();
            final long totalSalesmen = salesmanRepository.totalUniqueSalesman();

            final Sales mostExpensive = salesRepository.findMostExpensive();
            final Sales worstSeller = salesRepository.findWorstSellers();

            final FileWriter writer = new FileWriter(outputFileAbsolutePath);
            PrintWriter printer = new PrintWriter(writer);
            printer.println(String.format(messages.getMessage("job.sales.report.amount.customer"), totalCustomers));
            printer.println(String.format(messages.getMessage("job.sales.report.amount.salesman"), totalSalesmen));
            printer.println(String.format(messages.getMessage("job.sales.report.most.expensive"), mostExpensive.getSaleId()));
            printer.println(String.format(messages.getMessage("job.sales.report.worst.seller"), worstSeller.getSalesmanName()));
            printer.close();

            return RepeatStatus.FINISHED;
        };
    }

    /**
     * The simple task to clean all register used in job.
     *
     * @return tasklet reset execution.
     */
    @Bean
    public Tasklet reset() {
        return (contribution, chunkContext) -> {
            customerRepository.deleteAll();
            salesmanRepository.deleteAll();
            salesRepository.deleteAll();
            return RepeatStatus.FINISHED;
        };
    }

    /**
     * The simple task executed when occurs an error on generated report.
     *
     * @return tasklet error execution.
     */
    @Bean
    public Tasklet error() {
        return (contribution, chunkContext) -> {
            final Map<String, Object> parameters = chunkContext.getStepContext().getJobParameters();
            final String outputFileAbsolutePath = (String) parameters.get(JobConstants.OUTPUT_FILE_ABSOLUTE_PATH_ERROR_PARAM);

            FileWriter writer = new FileWriter(outputFileAbsolutePath);
            PrintWriter printer = new PrintWriter(writer);
            printer.println(String.format(messages.getMessage("job.sales.report.error"), "Error."));
            printer.close();
            return RepeatStatus.FINISHED;
        };
    }
}
