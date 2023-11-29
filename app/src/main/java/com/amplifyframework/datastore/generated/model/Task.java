package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;
import com.amplifyframework.core.model.temporal.Temporal;

import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.AuthStrategy;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelOperation;
import com.amplifyframework.core.model.annotations.AuthRule;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the Task type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "Tasks", authRules = {
  @AuthRule(allow = AuthStrategy.PUBLIC, operations = { ModelOperation.CREATE, ModelOperation.UPDATE, ModelOperation.DELETE, ModelOperation.READ })
})
@Index(name = "byTeam", fields = {"teamId","title"})
public final class Task implements Model {
  public static final QueryField ID = field("Task", "id");
  public static final QueryField TITLE = field("Task", "title");
  public static final QueryField BODY = field("Task", "body");
  public static final QueryField STATE = field("Task", "state");
  public static final QueryField TASK_IMAGE_S3_KEY = field("Task", "taskImageS3Key");
  public static final QueryField TASK_LONGITUDE = field("Task", "taskLongitude");
  public static final QueryField TASK_LATITUDE = field("Task", "taskLatitude");
  public static final QueryField TEAM_NAME = field("Task", "teamId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String title;
  private final @ModelField(targetType="String") String body;
  private final @ModelField(targetType="State") State state;
  private final @ModelField(targetType="String") String taskImageS3Key;
  private final @ModelField(targetType="String") String taskLongitude;
  private final @ModelField(targetType="String") String taskLatitude;
  private final @ModelField(targetType="Team") @BelongsTo(targetName = "teamId", type = Team.class) Team teamName;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime createdAt;
  private @ModelField(targetType="AWSDateTime", isReadOnly = true) Temporal.DateTime updatedAt;
  /** @deprecated This API is internal to Amplify and should not be used. */
  @Deprecated
   public String resolveIdentifier() {
    return id;
  }
  
  public String getId() {
      return id;
  }
  
  public String getTitle() {
      return title;
  }
  
  public String getBody() {
      return body;
  }
  
  public State getState() {
      return state;
  }
  
  public String getTaskImageS3Key() {
      return taskImageS3Key;
  }
  
  public String getTaskLongitude() {
      return taskLongitude;
  }
  
  public String getTaskLatitude() {
      return taskLatitude;
  }
  
  public Team getTeamName() {
      return teamName;
  }
  
  public Temporal.DateTime getCreatedAt() {
      return createdAt;
  }
  
  public Temporal.DateTime getUpdatedAt() {
      return updatedAt;
  }
  
  private Task(String id, String title, String body, State state, String taskImageS3Key, String taskLongitude, String taskLatitude, Team teamName) {
    this.id = id;
    this.title = title;
    this.body = body;
    this.state = state;
    this.taskImageS3Key = taskImageS3Key;
    this.taskLongitude = taskLongitude;
    this.taskLatitude = taskLatitude;
    this.teamName = teamName;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      Task task = (Task) obj;
      return ObjectsCompat.equals(getId(), task.getId()) &&
              ObjectsCompat.equals(getTitle(), task.getTitle()) &&
              ObjectsCompat.equals(getBody(), task.getBody()) &&
              ObjectsCompat.equals(getState(), task.getState()) &&
              ObjectsCompat.equals(getTaskImageS3Key(), task.getTaskImageS3Key()) &&
              ObjectsCompat.equals(getTaskLongitude(), task.getTaskLongitude()) &&
              ObjectsCompat.equals(getTaskLatitude(), task.getTaskLatitude()) &&
              ObjectsCompat.equals(getTeamName(), task.getTeamName()) &&
              ObjectsCompat.equals(getCreatedAt(), task.getCreatedAt()) &&
              ObjectsCompat.equals(getUpdatedAt(), task.getUpdatedAt());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getTitle())
      .append(getBody())
      .append(getState())
      .append(getTaskImageS3Key())
      .append(getTaskLongitude())
      .append(getTaskLatitude())
      .append(getTeamName())
      .append(getCreatedAt())
      .append(getUpdatedAt())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("Task {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("title=" + String.valueOf(getTitle()) + ", ")
      .append("body=" + String.valueOf(getBody()) + ", ")
      .append("state=" + String.valueOf(getState()) + ", ")
      .append("taskImageS3Key=" + String.valueOf(getTaskImageS3Key()) + ", ")
      .append("taskLongitude=" + String.valueOf(getTaskLongitude()) + ", ")
      .append("taskLatitude=" + String.valueOf(getTaskLatitude()) + ", ")
      .append("teamName=" + String.valueOf(getTeamName()) + ", ")
      .append("createdAt=" + String.valueOf(getCreatedAt()) + ", ")
      .append("updatedAt=" + String.valueOf(getUpdatedAt()))
      .append("}")
      .toString();
  }
  
  public static TitleStep builder() {
      return new Builder();
  }
  
  /**
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   */
  public static Task justId(String id) {
    return new Task(
      id,
      null,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      title,
      body,
      state,
      taskImageS3Key,
      taskLongitude,
      taskLatitude,
      teamName);
  }
  public interface TitleStep {
    BuildStep title(String title);
  }
  

  public interface BuildStep {
    Task build();
    BuildStep id(String id);
    BuildStep body(String body);
    BuildStep state(State state);
    BuildStep taskImageS3Key(String taskImageS3Key);
    BuildStep taskLongitude(String taskLongitude);
    BuildStep taskLatitude(String taskLatitude);
    BuildStep teamName(Team teamName);
  }
  

  public static class Builder implements TitleStep, BuildStep {
    private String id;
    private String title;
    private String body;
    private State state;
    private String taskImageS3Key;
    private String taskLongitude;
    private String taskLatitude;
    private Team teamName;
    public Builder() {
      
    }
    
    private Builder(String id, String title, String body, State state, String taskImageS3Key, String taskLongitude, String taskLatitude, Team teamName) {
      this.id = id;
      this.title = title;
      this.body = body;
      this.state = state;
      this.taskImageS3Key = taskImageS3Key;
      this.taskLongitude = taskLongitude;
      this.taskLatitude = taskLatitude;
      this.teamName = teamName;
    }
    
    @Override
     public Task build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new Task(
          id,
          title,
          body,
          state,
          taskImageS3Key,
          taskLongitude,
          taskLatitude,
          teamName);
    }
    
    @Override
     public BuildStep title(String title) {
        Objects.requireNonNull(title);
        this.title = title;
        return this;
    }
    
    @Override
     public BuildStep body(String body) {
        this.body = body;
        return this;
    }
    
    @Override
     public BuildStep state(State state) {
        this.state = state;
        return this;
    }
    
    @Override
     public BuildStep taskImageS3Key(String taskImageS3Key) {
        this.taskImageS3Key = taskImageS3Key;
        return this;
    }
    
    @Override
     public BuildStep taskLongitude(String taskLongitude) {
        this.taskLongitude = taskLongitude;
        return this;
    }
    
    @Override
     public BuildStep taskLatitude(String taskLatitude) {
        this.taskLatitude = taskLatitude;
        return this;
    }
    
    @Override
     public BuildStep teamName(Team teamName) {
        this.teamName = teamName;
        return this;
    }
    
    /**
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     */
    public BuildStep id(String id) {
        this.id = id;
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String title, String body, State state, String taskImageS3Key, String taskLongitude, String taskLatitude, Team teamName) {
      super(id, title, body, state, taskImageS3Key, taskLongitude, taskLatitude, teamName);
      Objects.requireNonNull(title);
    }
    
    @Override
     public CopyOfBuilder title(String title) {
      return (CopyOfBuilder) super.title(title);
    }
    
    @Override
     public CopyOfBuilder body(String body) {
      return (CopyOfBuilder) super.body(body);
    }
    
    @Override
     public CopyOfBuilder state(State state) {
      return (CopyOfBuilder) super.state(state);
    }
    
    @Override
     public CopyOfBuilder taskImageS3Key(String taskImageS3Key) {
      return (CopyOfBuilder) super.taskImageS3Key(taskImageS3Key);
    }
    
    @Override
     public CopyOfBuilder taskLongitude(String taskLongitude) {
      return (CopyOfBuilder) super.taskLongitude(taskLongitude);
    }
    
    @Override
     public CopyOfBuilder taskLatitude(String taskLatitude) {
      return (CopyOfBuilder) super.taskLatitude(taskLatitude);
    }
    
    @Override
     public CopyOfBuilder teamName(Team teamName) {
      return (CopyOfBuilder) super.teamName(teamName);
    }
  }

}
