package org.approvalsj.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.String.format;
import static java.nio.file.Files.*;
import static org.approvalsj.util.FileUtils.*;
import static org.approvalsj.util.FileUtils.silentRead;

public class TestClassCompanion {
    private final Class<?> testClass;
    private final Path folder;
    private final StackUtils stackUtils;


    public TestClassCompanion(Class<?> testClass) {
        this.testClass = testClass;
        this.folder = folderForClass();
        this.stackUtils = new StackUtils(testClass);
    }

    public void writeApproved(String content) {
        Path approvedFile = approvedFile();
        write(content, approvedFile);
    }

    public String readApproved() {
        return silentRead(approvedFile());
    }

    public void removeApproved() {
        silentRemove(approvedFile());
    }

    public void writeReceived(String content) {
        Path receivedFile = receivedFile();
        write(content, receivedFile);
    }

    public String readReceived() {
        return silentRead(receivedFile());
    }

    public void removeReceived() {
        silentRemove(receivedFile());
    }

    public Path approvedFile() {
        return approvedFile(stackUtils.methodName().get());
    }

    public Path receivedFile() {
        return receivedFile(stackUtils.methodName().get());
    }

    Path receivedFile(String methodName) {
        String fileName = format("%s.received", methodName);
        return folder.resolve(fileName);
    }

    Path approvedFile(String methodName) {
        String fileName = format("%s.approved", methodName);
        return folder.resolve(fileName);
    }


    private Path folderForClass() {
        String packageName = testClass.getPackage().getName();
        Path packageResourcesPath = Paths.get("src/test/resources/", packageName.split("\\."));
        return packageResourcesPath.resolve(testClass.getSimpleName());
    }

    private void write(String content, Path file) {
        try {
            createDirectories(file.getParent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (BufferedWriter writer = newBufferedWriter(file)) {
            writer.write(content);
        } catch (IOException e) {
            String message = format("Could not write to file %s because of %s", file.toAbsolutePath(), e);
            throw new RuntimeException(message, e);
        }
    }
}
