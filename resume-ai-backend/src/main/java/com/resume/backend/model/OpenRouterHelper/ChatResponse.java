package com.resume.backend.model.OpenRouterHelper;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatResponse  {
  private String id;
  private String provider;
  private String model;
  private String object;
  private long created;
  private List<Choice> choices;
  private Usage usage;
  
  public String getId() {
      return id;
  }
  
  public void setId(String id) {
      this.id = id;
  }
  
  public String getProvider() {
      return provider;
  }
  
  public void setProvider(String provider) {
      this.provider = provider;
  }
  
  public String getModel() {
      return model;
  }
  
  public void setModel(String model) {
      this.model = model;
  }
  
  public String getObject() {
      return object;
  }
  
  public void setObject(String object) {
      this.object = object;
  }
  
  public long getCreated() {
      return created;
  }
  
  public void setCreated(long created) {
      this.created = created;
  }
  
  public List<Choice> getChoices() {
      return choices;
  }
  
  public void setChoices(List<Choice> choices) {
      this.choices = choices;
  }
  
  public Usage getUsage() {
      return usage;
  }
  
  public void setUsage(Usage usage) {
      this.usage = usage;
  }
}
