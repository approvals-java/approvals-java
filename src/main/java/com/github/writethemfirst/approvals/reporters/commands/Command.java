package com.github.writethemfirst.approvals.reporters.commands;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static java.lang.Runtime.getRuntime;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static java.nio.file.Files.find;
import static java.nio.file.Paths.get;
import static java.util.Arrays.stream;
import static java.util.Comparator.reverseOrder;
import static java.util.Optional.empty;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

/**
 * Wrapper around an executable command outside the JVM.
 */
public class Command {
    private static final int MAX_FOLDERS_DEPTH = 5;
    private final String path;
    private final String executable;
    private final Runtime runtime;

    Command(String path, String executable) {
        this(path, executable, getRuntime());
    }

    /**
     * Only use this constructor from test code so the Runtime can be mocked.
     */
    Command(String path, String executable, Runtime runtime) {
        this.path = path;
        this.executable = executable;
        this.runtime = runtime;
    }

    // TODO: cache the path
    // TODO: method to check if available
    // TODO: programfiles aware

    /**
     * Finds the latest version of an installed software.
     *
     * Sort order is based on folder names, assuming that latest version have a greater version number.
     */
    public Optional<String> pathToExe() {
        try {
            return matchingCommands()
                .map(Path::toString)
                .sorted(reverseOrder())
                .findFirst();
        } catch (IOException e) {
            System.err.println(e);
            return empty();
        }
    }

    private Stream<Path> matchingCommands() throws IOException {
        return find(
            get(path),
            MAX_FOLDERS_DEPTH,
            (p, a) -> p.endsWith(executable),
            FOLLOW_LINKS);
    }

    public void execute(String... arguments) throws IOException {
        String[] cmdArray = concat(
            of(pathToExe().get()),
            stream(arguments))
            .toArray(String[]::new);
        runtime.exec(cmdArray);
    }
}
