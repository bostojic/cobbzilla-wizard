package org.cobbzilla.wizard.task;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@ToString
public class TaskResult {

    @Getter @Setter private String actionMessageKey;
    @Getter @Setter private String target;

    @Getter @Setter private String returnValue;

    @Getter @Setter private boolean success = false;
    @Getter @Setter @JsonIgnore private Exception exception;

    /** an error message for the task */
    public String getError () { return exception == null ? null : exception.toString(); }
    public void setError (String error) { exception = new Exception(error); }

    private final List<TaskEvent> events = new ArrayList<>();
    public List<TaskEvent> getEvents () {
        synchronized (events) {
            return new ArrayList<>(events);
        }
    }

    public void add(TaskEvent event) {
        synchronized (events) {
            this.events.add(event);
        }
    }

    public void error(TaskEvent event, Exception e) {
        add(event);
        this.exception = e;
    }

    public void description (String actionMessageKey, String target) {
        setActionMessageKey(actionMessageKey);
        setTarget(target);
    }

    @JsonIgnore public boolean isComplete () { return success || getError() != null; }

}
