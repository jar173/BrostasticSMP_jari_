package jari_.brostasticsmp.arguments;

import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class DurationArgument implements ArgumentType<String> {
    private static final List<String> EXAMPLES = Arrays.asList("24h", "7d", "1w", "1m", "1y");
    private static final Pattern TIME_PATTERN = Pattern.compile("^(\\d+)([hdwmy])$");
    private static final List<String> UNITS = Arrays.asList("h", "d", "w", "m", "y");

    public static DurationArgument duration() {
        return new DurationArgument();
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String input = reader.readString();
        if (!TIME_PATTERN.matcher(input).matches()) {
            Message message = Text.literal("Invalid duration format. Use: <number><unit> where unit is h(hours), d(days), w(weeks), m(months), y(years)");
            throw new CommandSyntaxException(null, message, input, 0);
        }
        return input;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String input = builder.getRemaining();

        // If input ends with a number, suggest all units
        if (input.matches("\\d+")) {
            UNITS.forEach(unit -> builder.suggest(input + unit));
        }

        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
}
