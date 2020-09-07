package com.salesreport.batch;

import com.salesreport.config.ConfigBatchProperties;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

@Component
public class SalesReportJobInitializer implements ApplicationRunner {

    public static final String INPUT = System.getenv("HOMEPATH") + File.separator + "data" + File.separator + "in";
    public static final String OUTPUT = System.getenv("HOMEPATH") + File.separator + "data" + File.separator + "out";

    @Autowired
    private ConfigBatchProperties jobProperties;
    @Autowired
    private JobLauncher jobLauncher;
    @Autowired
    private Job job;

    /**
     * The runnable sales report job initializer.
     * @param arg0
     */
    @Override
    public void run(ApplicationArguments arg0) {
        final ConfigBatchProperties.JobProperties salesReport = jobProperties.getSalesReport();
        final Path filePath = Paths.get(INPUT);


        try {
            scanAlreadyExistentFiles();

            final WatchService watchService = FileSystems.getDefault().newWatchService();
            filePath.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);

            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    final Path path = filePath.resolve((Path) event.context()).toAbsolutePath();
                    if (path.getFileName().toString().trim().endsWith(salesReport.getFileType())) {
                        startJob(path);
                    }
                    key.reset();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check for existent file to process
     * @throws IOException
     * @throws JobParametersInvalidException
     * @throws JobExecutionAlreadyRunningException
     * @throws JobRestartException
     * @throws JobInstanceAlreadyCompleteException
     */
    private void scanAlreadyExistentFiles() throws IOException, JobParametersInvalidException,
            JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        final ConfigBatchProperties.JobProperties salesReport = jobProperties.getSalesReport();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(INPUT))) {
            for (Path path : directoryStream) {
                if (path.getFileName().toString().trim().endsWith(salesReport.getFileType())) {
                    startJob(path.toAbsolutePath());
                }
            }
        }
    }

    /**
     * Start the sales report job.
     * @param path
     * @throws JobParametersInvalidException
     * @throws JobExecutionAlreadyRunningException
     * @throws JobRestartException
     * @throws JobInstanceAlreadyCompleteException
     */
    private void startJob(final Path path) throws JobParametersInvalidException, JobExecutionAlreadyRunningException,
            JobRestartException, JobInstanceAlreadyCompleteException {

        final ConfigBatchProperties.JobProperties salesReport = jobProperties.getSalesReport();
        final String fileType = salesReport.getFileType();

        Map<String, JobParameter> maps = new HashMap<>();
        maps.put(JobConstants.TIME_PARAM, new JobParameter(System.currentTimeMillis()));
        maps.put(JobConstants.FILE_NAME_PARAM, new JobParameter(path.normalize().getFileName().toString()));
        maps.put(JobConstants.FILE_ABSOLUTE_PATH_PARAM, new JobParameter(path.normalize().toString()));
        maps.put(JobConstants.FILE_PARENT_FOLDER_PARAM, new JobParameter(path.normalize().getParent().toString()));
        maps.put(JobConstants.OUTPUT_FILE_ABSOLUTE_PATH_SUCCESS_PARAM, new JobParameter(OUTPUT
                + File.separator + path.normalize().getFileName().toString().replace(fileType, ".done" + fileType)));
        maps.put(JobConstants.OUTPUT_FILE_ABSOLUTE_PATH_ERROR_PARAM, new JobParameter(OUTPUT
                + File.separator + path.normalize().getFileName().toString().replace(fileType, ".error" + fileType)));

        JobParameters parameters = new JobParameters(maps);
        jobLauncher.run(job, parameters);
    }

}
