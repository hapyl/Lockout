package me.hapyl.lockout.board.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.hapyl.lockout.Lockout;
import me.hapyl.lockout.PlayerTeam;
import me.hapyl.lockout.board.Board;
import me.hapyl.lockout.util.Icon;
import me.hapyl.lockout.util.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class Tasks {
    
    public static final Task GOURMAND;
    public static final Task SMELTING_SPREE;
    public static final Task THE_LIE;
    public static final Task MONSTER_SLAYER;
    public static final Task USED_UP;
    public static final Task MANNEQUIN;
    public static final Task TRY_NOT_TO_DIE_CHALLENGE;
    public static final Task PICKUP_NAMED_ITEM_B;
    public static final Task PICKUP_NAMED_ITEM_C;
    public static final Task PICKUP_NAMED_ITEM_G;
    public static final Task PICKUP_NAMED_ITEM_L;
    public static final Task PICKUP_NAMED_ITEM_P;
    public static final Task DEEP_POCKETS;
    public static final Task LOCKED_AND_LOADED;
    public static final Task FULLY_STACKED;
    public static final Task TOUCH_THE_SKY;
    public static final Task BUCKET_LIST;
    public static final Task THE_AUTHOR;
    public static final Task HELLO_MY_NAME_IS;
    public static final Task FLAG_WEAVER;
    public static final Task EGOISTIC;
    public static final Task IRON_MAN;
    public static final Task ARCHEOLOGIST;
    public static final Task TOMB_RAIDER;
    public static final Task THE_RAINBOW;
    public static final Task STYLIST;
    public static final Task QUIET_PLACE;
    public static final Task ROYAL_DINNER;
    public static final Task SOUPER_CHEF;
    public static final Task LAST_MEAL;
    public static final Task PYROTECHNICS;
    public static final Task HAVE_A_SEAT;
    public static final Task ACHIEVEMENT_HUNTER;
    public static final Task GREEN_THUMB;
    public static final Task LEVEL_UP;
    public static final Task ONE_MANS_TRASH;
    
    private static final Map<Key, Task> REGISTRY;
    private static final Map<? extends TaskDifficulty, ? extends List<? extends Task>> BY_DIFFICULTY;
    
    static {
        REGISTRY = Maps.newHashMap();
        
        // Register tasks below
        GOURMAND = register("gourmand", TaskGourmand::new);
        SMELTING_SPREE = register("smelting_spree", TaskSmeltingSpree::new);
        THE_LIE = register("the_lie", TaskTheLie::new);
        MONSTER_SLAYER = register("monster_slayer", TaskMonsterSlayer::new);
        USED_UP = register("used_up", TaskUsedUp::new);
        MANNEQUIN = register("mannequin", TaskMannequin::new);
        TRY_NOT_TO_DIE_CHALLENGE = register("try_not_to_die_challenge", TaskTryNotToDieChallenge::new);
        PICKUP_NAMED_ITEM_B = register("pickup_named_item_b", key -> new TaskPickupNamedItem(key, TaskPickupNamedItem.Letter.B));
        PICKUP_NAMED_ITEM_C = register("pickup_named_item_c", key -> new TaskPickupNamedItem(key, TaskPickupNamedItem.Letter.C));
        PICKUP_NAMED_ITEM_G = register("pickup_named_item_g", key -> new TaskPickupNamedItem(key, TaskPickupNamedItem.Letter.G));
        PICKUP_NAMED_ITEM_L = register("pickup_named_item_l", key -> new TaskPickupNamedItem(key, TaskPickupNamedItem.Letter.L));
        PICKUP_NAMED_ITEM_P = register("pickup_named_item_p", key -> new TaskPickupNamedItem(key, TaskPickupNamedItem.Letter.P));
        DEEP_POCKETS = register("deep_pockets", TaskDeepPockets::new);
        LOCKED_AND_LOADED = register("locked_and_loaded", TaskLockedAndLoaded::new);
        FULLY_STACKED = register("fully_stacked", TaskFullyStacked::new);
        TOUCH_THE_SKY = register("touch_the_sky", TaskTouchTheSky::new);
        BUCKET_LIST = register("bucket_list", TaskBucketList::new);
        THE_AUTHOR = register("the_author", TaskTheAuthor::new);
        HELLO_MY_NAME_IS = register("hello_my_name_is", TaskHelloMyNameIs::new);
        FLAG_WEAVER = register("flag_weaver", TaskFlagWeaver::new);
        EGOISTIC = register("egoistic", TaskEgoistic::new);
        IRON_MAN = register("iron_man", TaskIronMan::new);
        ARCHEOLOGIST = register("archeologist", TaskArcheologist::new);
        TOMB_RAIDER = register("tomb_raider", TaskTombRaider::new);
        THE_RAINBOW = register("the_rainbow", TaskTheRainbow::new);
        STYLIST = register("stylist", TaskStylist::new);
        QUIET_PLACE = register("quiet_place", TaskQuietPlace::new);
        ROYAL_DINNER = register("royal_dinner", TaskRoyalDinner::new);
        SOUPER_CHEF = register("souper_chef", TaskSouperChef::new);
        LAST_MEAL = register("last_meal", TaskLastMeal::new);
        PYROTECHNICS = register("pyrotechnics", TaskPyrotechnics::new);
        HAVE_A_SEAT = register("have_a_set", TaskHaveASeat::new);
        ACHIEVEMENT_HUNTER = register("achievement_hunter", TaskAchievementHunter::new);
        GREEN_THUMB = register("green_thumb", TaskGreenThumb::new);
        LEVEL_UP = register("level_up", TaskLevelUp::new);
        ONE_MANS_TRASH = register("one_mans_trash", TaskOneMansTrash::new);
        
        // Group tasks by the difficulty
        BY_DIFFICULTY = REGISTRY.values().stream().collect(Collectors.groupingBy(Task::getDifficulty, Collectors.toList()));
    }
    
    private Tasks() {
    }
    
    public static @NotNull Map<? extends Key, ? extends Task> tasks() {
        return Map.copyOf(REGISTRY);
    }
    
    public static @NotNull List<? extends Task> generateTasks() {
        if (true) {
            validateThereIsEnoughTasksToGenerateTheBoard();
        }
        
        final List<Task> tasks = Lists.newArrayList();
        final Map<TaskCategory, Integer> seenByCategory = Maps.newEnumMap(TaskCategory.class);
        
        // Iterate over difficulties in reverse order
        for (TaskDifficulty taskDifficulty : TaskDifficulty.VALUES_IN_REVERSE_ORDER) {
            final List<? extends Task> taskPool = new ArrayList<>(BY_DIFFICULTY.get(taskDifficulty));
            int seenByDifficulty = 0;
            
            // Shuffle the tasks to randomize them
            Collections.shuffle(taskPool);
            
            for (Task task : taskPool) {
                final TaskCategory category = task.getCategory();
                
                // If we hit the category limit, continue
                if (seenByCategory.getOrDefault(category, 0) >= category.getMaximumNumberOfTasks()) {
                    continue;
                }
                
                // If we hit quota for the difficulty, break
                if (seenByDifficulty >= taskDifficulty.getMaximumNumberOfTasks()) {
                    break;
                }
                
                tasks.add(task);
                
                seenByCategory.merge(category, 1, Integer::sum);
                seenByDifficulty++;
                
                // If we hit the threshold, break early
                if (tasks.size() == Board.BOARD_SIZE) {
                    // Shuffle the tasks again and return
                    Collections.shuffle(tasks);
                    return tasks;
                }
            }
        }
        
        // If we hit this point, it means there isn't enough tasks, which should never happen
        throw new IllegalStateException("There isn't enough tasks to generate! (%s/%s)".formatted(tasks.size(), Board.BOARD_SIZE));
    }
    
    private static void validateThereIsEnoughTasksToGenerateTheBoard() {
        for (Map.Entry<? extends TaskDifficulty, ? extends List<? extends Task>> entry : BY_DIFFICULTY.entrySet()) {
            final TaskDifficulty difficulty = entry.getKey();
            final List<? extends Task> tasks = entry.getValue();
            
            final long size = tasks.stream()
                                   .collect(Collectors.groupingBy(Task::getCategory, Collectors.counting()))
                                   .entrySet()
                                   .stream()
                                   .mapToLong(group -> Math.min(group.getKey().getMaximumNumberOfTasks(), group.getValue()))
                                   .sum();
            
            if (size < difficulty.getMaximumNumberOfTasks()) {
                throw new IllegalStateException("There isn't enough tasks with %s difficulty to create a board! (%s/%s)".formatted(difficulty, size, difficulty.getMaximumNumberOfTasks()));
            }
        }
    }
    
    private static <T extends Task> @NotNull T register(@NotNull String key, @NotNull Function<Key, T> constructor) {
        final Key theKey = Key.ofString(key);
        
        if (REGISTRY.containsKey(theKey)) {
            throw new IllegalArgumentException("Duplicate key: " + key);
        }
        
        final T task = constructor.apply(theKey);
        REGISTRY.put(theKey, task);
        
        return task;
    }
    
}