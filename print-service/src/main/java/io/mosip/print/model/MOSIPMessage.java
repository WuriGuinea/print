package io.mosip.print.model;

//JSON Serializable
public class MOSIPMessage {
    private String publisher;
    private String topic;
    private String publishedOn;
    //Reverse the event and payload based on serialize or deserialize
    private String payload; //JWT <header>.<eventpayload>.<signature>
    //interchange with payload do not serialize
    Event event; //JWT <eventpayload> deserilized


 // Getter Methods 

 public String getPublisher() {
  return publisher;
 }

 public String getTopic() {
  return topic;
 }

 public String getPublishedOn() {
  return publishedOn;
 }

 public Event getEvent() {
  return event;
 }

 public String getPayload(){
	 return payload;
 }

 // Setter Methods 

 public void setPublisher(String publisher) {
  this.publisher = publisher;
 }

 public void setTopic(String topic) {
  this.topic = topic;
 }

 public void setPublishedOn(String publishedOn) {
  this.publishedOn = publishedOn;
 }

 public void setEvent(Event eventObject) {
  this.event = eventObject;
 }



}