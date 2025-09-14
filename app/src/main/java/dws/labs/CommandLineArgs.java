package dws.labs;

import com.beust.jcommander.Parameter;

import lombok.Getter;

@Getter
public class CommandLineArgs {
    @Parameter(names = "-n", description = "Programmers num")
    private int programmersNum = 3;

    @Parameter(names = "--soup-required", description = "Programmers need soup")
    private int programmerNeedsSoup = 50;

    @Parameter(names = "--soup", description = "Soup total amount")
    private int soupAmount = 150;

    @Parameter(names = "--bowl", description = "Bowl size")
    private int bowlSize = 25;
}
