/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package sandbox;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * @author Almas Baimagambetov (almaslvl@gmail.com)
 */
public class ChangelogGen {

    public static void main(String[] args) throws Exception {
        var supportedTypes = List.of(
                "build",
                "docs",
                "feat",
                "fix",
                "perf",
                "refactor",
                "test",
                "repo"
        );

        var output = new ArrayList<String>();
        output.add("## Auto-generated changelog\n\n");

        var map = Files.readAllLines(Paths.get("CHANGELOG_RAW.md"))
                .stream()
                .filter(line -> {
                    return supportedTypes.stream().anyMatch(prefix -> line.startsWith(prefix))
                            && line.split(" ")[0].endsWith(":");
                })
                .map(line -> {
                    var type = line.substring(0, line.indexOf(':')).trim();
                    var isBreaking = type.endsWith("!");
                    var scope = type.contains("(") ? extractScope(type) : "";
                    var message = line.substring(line.indexOf(':') + 1).trim();

                    return new Change(type, scope, message, isBreaking);
                })
                .collect(Collectors.groupingBy(Change::type));

        new TreeMap<>(map)
                .forEach((type, changes) -> {
                    output.add("### " + type + "\n");

                    changes.forEach(change -> {
                        output.add("* " + change.message() + (change.isBreaking() ? " -- **BREAKING**" : ""));
                    });

                    output.add("\n");
                });

        Files.write(Paths.get("CHANGELOG.md"), output);
    }

    private static String extractScope(String type) {
        if (!type.contains("("))
            return "";

        var scope = type.substring(type.indexOf('(') + 1);
        scope = scope.substring(0, scope.indexOf(')'));

        return scope;
    }

    private record Change(
            String type,
            String scope,
            String message,
            boolean isBreaking
    ) {}
}
