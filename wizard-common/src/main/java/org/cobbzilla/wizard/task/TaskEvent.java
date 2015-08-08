package org.cobbzilla.wizard.task;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor @ToString
public class TaskEvent {

    @Getter @Setter private String taskId;
    @Getter @Setter private String messageKey;
    @Getter @Setter private long ctime = System.currentTimeMillis();

    public TaskEvent(TaskBase task, String messageKey) {
        this.taskId = task.getTaskId().getUuid();
        this.messageKey = messageKey;
    }

}
