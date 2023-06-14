package jungle.ovengers.exception;

public class TodoNotFoundException extends RuntimeException{
    private final Long todoId;

    public TodoNotFoundException(Long todoId) {
        this.todoId = todoId;
    }

    @Override
    public String getMessage() {
        return "존재하지 않는 todo입니다. todoId: " + todoId;
    }
}
