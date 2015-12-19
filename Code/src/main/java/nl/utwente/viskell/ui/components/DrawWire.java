package nl.utwente.viskell.ui.components;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.shape.CubicCurve;
import javafx.scene.transform.Transform;
import nl.utwente.viskell.ui.BlockContainer;
import nl.utwente.viskell.ui.ComponentLoader;
import nl.utwente.viskell.ui.CustomUIPane;

/**
 * A DrawWire represents the UI for a new incomplete connection is the process of being drawn. 
 * It is linked to a single Anchor as starting point, and with a second anchor it produces a new Connection.
 */
public class DrawWire extends CubicCurve implements ChangeListener<Transform>, ComponentLoader {

    /** The Pane this connection wire is on. */
    private final CustomUIPane pane;

    /** The Anchor this wire has been initiated from */
    private final ConnectionAnchor anchor;
    
    /**
     * @param pane The Pane this wire is on.
     * @param anchor The starting anchor of new wire.
     */
    public DrawWire(CustomUIPane pane, ConnectionAnchor anchor) {
        this.setMouseTransparent(true);

        this.pane = pane;
        this.anchor = anchor;
        pane.addWire(this);
        Point2D initPos = pane.sceneToLocal(anchor.localToScene(new Point2D(0, 0)));
        this.setFreePosition(initPos);
        this.invalidateAnchorPosition();
        anchor.localToSceneTransformProperty().addListener(x -> this.invalidateAnchorPosition());
    }

    /**
     * Constructs a new Connection from this partial wire and another anchor
     * @param target the Anchor to which the other end of this should be connection to.
     * @return the newly build Connection or null if it's not possible
     */
    public Connection buildConnectionTo(ConnectionAnchor target) {
        InputAnchor sink;
        OutputAnchor source;
        if (this.anchor instanceof InputAnchor) {
            if (target instanceof InputAnchor) {
                return null;
            }
            sink = (InputAnchor)this.anchor;
            source = (OutputAnchor)target;
        } else {
            if (target instanceof OutputAnchor) {
                return null;
            }
            sink = (InputAnchor)target;
            source = (OutputAnchor)this.anchor;
            
            if (sink.hasConnection()) {
                sink.removeConnections(); // push out the existing connection
            }
        }

        return new Connection(this.pane, source, sink);
    }

    /** Removes this wire from its pane, and its listener. */
    public final void remove() {
        this.anchor.localToSceneTransformProperty().removeListener(this);
        this.pane.removeWire(this);
    }

    @Override
    public void changed(ObservableValue<? extends Transform> observable, Transform oldValue, Transform newValue) {
        this.invalidateAnchorPosition();
    }

    /** Update the UI position of the anchor. */
    private void invalidateAnchorPosition() {
        Point2D center = (this.anchor instanceof InputAnchor) ? new Point2D(0, -4) : new Point2D(0, 4);
        Point2D point = pane.sceneToLocal(this.anchor.localToScene(center));
        this.setStartX(point.getX());
        this.setStartY(point.getY());
        this.updateBezierControlPoints();
    }
    
    /**
     * Sets the free end coordinates for this wire.
     * @param point coordinates local to this wire's parent.
     */
    public void setFreePosition(Point2D point) {
        this.setEndX(point.getX());
        this.setEndY(point.getY());
        this.updateBezierControlPoints();
        
        if (this.anchor instanceof OutputAnchor) {
            BlockContainer container = this.anchor.getContainer();
            if (container.getBoundsInScene().contains(this.pane.localToScene(point, false))) {
                this.getStrokeDashArray().clear();
            } else if (this.getStrokeDashArray().isEmpty()) {
                this.getStrokeDashArray().addAll(15.0, 15.0);
            }
        }
    }

    /** Updates the Bezier offset (curviness) according to the current start and end positions. */
    private void updateBezierControlPoints() {
        double BEZIER_CONTROL_OFFSET = Connection.BEZIER_CONTROL_OFFSET;
        double distY = Math.abs(this.getEndY() - this.getStartY());
        double yOffset = BEZIER_CONTROL_OFFSET;
        if (distY > BEZIER_CONTROL_OFFSET) {
            yOffset = Math.cbrt(distY / BEZIER_CONTROL_OFFSET) * BEZIER_CONTROL_OFFSET;
        }
        
        this.setControlX1(this.getStartX());
        this.setControlX2(this.getEndX());
        if (this.anchor instanceof OutputAnchor) {
            this.setControlY1(this.getStartY() + yOffset);
        } else {
            this.setControlY1(this.getStartY() - yOffset);
        }
        if (this.getStartY() > this.getEndY()) {
            this.setControlY2(this.getEndY()   + yOffset);
        } else {
            this.setControlY2(this.getEndY()   - yOffset);
        }
    }

}
