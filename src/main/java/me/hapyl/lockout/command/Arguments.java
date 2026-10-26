package me.hapyl.lockout.command;

import com.google.common.collect.ImmutableMap;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.argument.CustomArgumentType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class Arguments<E> implements CustomArgumentType<E, String> {
    
    private Arguments() {
    }
    
    public abstract @NotNull E parse(@NotNull String string) throws CommandSyntaxException;
    
    public abstract @NotNull Iterable<? extends String> suggestions();
    
    @Override
    public @NotNull E parse(@NotNull StringReader reader) throws CommandSyntaxException {
        return this.parse(reader.readString().toLowerCase());
    }
    
    @Override
    public @NotNull ArgumentType<String> getNativeType() {
        return StringArgumentType.word();
    }
    
    @Override
    public <S> @NotNull CompletableFuture<Suggestions> listSuggestions(@NotNull CommandContext<S> context, @NotNull SuggestionsBuilder builder) {
        final String remainingLowerCase = builder.getRemainingLowerCase();
        
        this.suggestions().forEach(suggestion -> {
            if (suggestion.startsWith(remainingLowerCase)) {
                builder.suggest(suggestion);
            }
        });
        
        return builder.buildFuture();
    }
    
    public static <E extends Enum<E>> @NotNull Arguments<E> enums(@NotNull Class<E> enumClass) {
        return new ArgumentsEnumImpl<>(enumClass);
    }
    
    public static class ArgumentsEnumImpl<E extends Enum<E>> extends Arguments<E> {
        
        private static final DynamicCommandExceptionType EXCEPTION_TYPE = new DynamicCommandExceptionType(object -> new LiteralMessage("Unknown argument: " + object));
        
        private final Map<String, E> enumConstantsMapped;
        
        private ArgumentsEnumImpl(@NotNull Class<E> enumClass) {
            this.enumConstantsMapped = Arrays.stream(enumClass.getEnumConstants()).collect(ImmutableMap.toImmutableMap(constant -> constant.name().toLowerCase(), constant -> constant));
        }
        
        @Override
        public @NotNull E parse(@NotNull String string) throws CommandSyntaxException {
            if (this.enumConstantsMapped.get(string) instanceof E e) {
                return e;
            }
            
            throw EXCEPTION_TYPE.create(string);
        }
        
        @Override
        public @NotNull Iterable<? extends String> suggestions() {
            return enumConstantsMapped.keySet();
        }
        
    }
    
}