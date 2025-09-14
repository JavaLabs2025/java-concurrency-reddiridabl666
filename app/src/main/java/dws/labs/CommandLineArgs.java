package dws.labs;

import com.beust.jcommander.Parameter;

import lombok.Getter;

@Getter
public class CommandLineArgs {
    @Parameter(names = "-n", description = "Programmers num")
    private int programmersNum = 3;

    @Parameter(names = "--portions-required", description = "Programmers need soup")
    private int programmersNeedPortions = 5;

    @Parameter(names = "--soup", description = "Soup total amount")
    private int soupAmount = programmersNum * programmersNeedPortions;

    @Parameter(names = "--waiters", description = "Waiters number")
    private int waiters = 2;

    @Parameter(names = "--timeout", description = "Timeout milliseconds")
    private int timeout = 100;
}
