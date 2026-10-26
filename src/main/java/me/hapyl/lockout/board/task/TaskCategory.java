package me.hapyl.lockout.board.task;

public enum TaskCategory implements MaximumNumberOfTasks{
    
    MISCELLANEOUS(10000),
    FOOD(2),
    PICKUP_NAMED_ITEM(1);
    
    private final int maximumNumberOfTasks;
    
     TaskCategory(int maximumNumberOfTasks) {
        this.maximumNumberOfTasks = maximumNumberOfTasks;
    }
    
    @Override
    public int getMaximumNumberOfTasks() {
        return maximumNumberOfTasks;
    }
    
}