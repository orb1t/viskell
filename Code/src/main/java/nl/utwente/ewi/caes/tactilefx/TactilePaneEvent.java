/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.utwente.ewi.caes.tactilefx;

import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.Node;

/**
 *
 * @author Richard
 */
public class TactilePaneEvent extends Event {
    public static final EventType<TactilePaneEvent> ANY = new EventType<>(
			Event.ANY, "ANY");
	public static final EventType<TactilePaneEvent> AREA_ENTERED = new EventType<>(
			ANY, "AREA_ENTERED");
        public static final EventType<TactilePaneEvent> IN_AREA = new EventType<>(
                        ANY, "IN_AREA");
	public static final EventType<TactilePaneEvent> AREA_LEFT = new EventType<>(
			ANY, "AREA_LEFT");
	
	private Node other;
		
	public TactilePaneEvent(EventType<TactilePaneEvent> eventType, Node target, Node otherNode) {
		super(eventType);
		this.target = target;
		this.other = otherNode;
	}
	
	/**
	 * Returns the target {@code Node} of this event
	 */
	public Node getTarget() {
            return (Node) target;
	}
	
	/**
	 * Returns the other {@code Node} that entered/left the target's proximity, or collided with it.
	 */
	public Node getOther() {
		return other;
	}
}
