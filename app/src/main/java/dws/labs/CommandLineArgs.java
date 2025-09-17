package dws.labs;

import com.beust.jcommander.Parameter;

import lombok.Getter;

@Getter
public class CommandLineArgs {
    @Parameter(names = "-n", description = "Programmers num")
    private int programmersNum = 7;

    @Parameter(names = "--soup", description = "Soup total amount")
    private int soupAmount = 10000;

    @Parameter(names = "--waiters", description = "Waiters number")
    private int waitersNum = 2;

    @Parameter(names = "--timeout", description = "Timeout milliseconds")
    private int timeout = 5000;
}
