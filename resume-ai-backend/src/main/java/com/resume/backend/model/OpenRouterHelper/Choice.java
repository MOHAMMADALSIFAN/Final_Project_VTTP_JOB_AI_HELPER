package com.resume.backend.model.OpenRouterHelper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Choice {
  private Message message;
        
  @JsonProperty("finish_reason")
  private String finishReason;
  
  @JsonProperty("native_finish_reason")
  private String nativeFinishReason;
  
  private int index;
  private Object logprobs;
  
  public Message getMessage() {
      return message;
  }
  
  public void setMessage(Message message) {
      this.message = message;
  }
  
  public String getFinishReason() {
      return finishReason;
  }
  
  public void setFinishReason(String finishReason) {
      this.finishReason = finishReason;
  }
  
  public String getNativeFinishReason() {
      return nativeFinishReason;
  }
  
  public void setNativeFinishReason(String nativeFinishReason) {
      this.nativeFinishReason = nativeFinishReason;
  }
  
  public int getIndex() {
      return index;
  }
  
  public void setIndex(int index) {
      this.index = index;
  }
  
  public Object getLogprobs() {
      return logprobs;
  }
  
  public void setLogprobs(Object logprobs) {
      this.logprobs = logprobs;
  }
}