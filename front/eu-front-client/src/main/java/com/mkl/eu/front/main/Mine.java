package com.mkl.eu.front.main;

import com.mkl.eu.front.map.handler.MapKeyboardHandler;
import com.mkl.eu.front.map.handler.MapMouseHandler;
import com.mkl.eu.front.map.handler.MultipleMapMouseHandler;
import com.mkl.eu.front.map.handler.ViewportRect;
import com.mkl.eu.front.map.marker.MarkerUtils;
import com.mkl.eu.front.map.marker.MyMarkerManager;
import com.mkl.eu.front.provider.EUProvider;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.events.EventDispatcher;
import de.fhpotsdam.unfolding.events.PanMapEvent;
import de.fhpotsdam.unfolding.events.ZoomMapEvent;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.interactions.KeyboardHandler;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.utils.ScreenPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PApplet;

import java.util.Map;

/**
 * Test PApplet to test the interactive Map.
 *
 * @author MKL
 */
public class Mine extends PApplet {
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(Mine.class);
    /** Interactive map. */
    private UnfoldingMap mapDetail;

    /** Small map showing the overview, i.e. the world. */
    private UnfoldingMap mapOverviewStatic;

    /** Interactive finder box atop the overview map. */
    private ViewportRect viewportRect;

    /**
     * Main method.
     * @param args no args.
     */
    public static void main(String args[]) {
        PApplet.main(new String[]{"com.mkl.eu.front.main.Mine"});
    }

    /** Set up the map and the markers. */
    public void setup() {
        size(1000, 600, OPENGL);

        mapDetail = new UnfoldingMap(this, "detail", new EUProvider(this));
        mapDetail.setTweening(true);
        mapDetail.zoomToLevel(7);
        mapDetail.setZoomRange(5, 10);
        mapDetail.panTo(1000, -300);


        // Static overview map
        mapOverviewStatic = new UnfoldingMap(this, "overviewStatic", 805, 10, 185, 185, true, false, new EUProvider(this), null);
        mapOverviewStatic.zoomToLevel(4);
        mapOverviewStatic.panTo(new Location(10, 7.5));

        viewportRect = new ViewportRect(this);

        EventDispatcher eventDispatcher = new EventDispatcher();
        KeyboardHandler keyboardHandler = new MapKeyboardHandler(this, mapDetail);
        MapMouseHandler mouseHandler = new MapMouseHandler(this, mapDetail);
        MultipleMapMouseHandler mouseHandler2 = new MultipleMapMouseHandler(this, mapOverviewStatic, viewportRect, mapDetail);

        eventDispatcher.addBroadcaster(keyboardHandler);
        eventDispatcher.addBroadcaster(mouseHandler);
        eventDispatcher.addBroadcaster(mouseHandler2);

        eventDispatcher.register(mapDetail, PanMapEvent.TYPE_PAN, mapDetail.getId());
        eventDispatcher.register(mapDetail, ZoomMapEvent.TYPE_ZOOM, mapDetail.getId());

        mapDetail.addMarkerManager(new MyMarkerManager());

        // Load country polygons and adds them as markers
        Map<String, Marker> countryMarkers = MarkerUtils.createMarkers(this);
        mapDetail.addMarkers(countryMarkers.values().toArray(new Marker[countryMarkers.values().size()]));
    }

    /** Draw the PApplet. */
    public void draw() {
        background(0);

        mapDetail.draw();
        mapOverviewStatic.draw();
        // Viewport is updated by the actual area of the detail map
        ScreenPosition tl = mapOverviewStatic.getScreenPosition(mapDetail.getTopLeftBorder());
        ScreenPosition br = mapOverviewStatic.getScreenPosition(mapDetail.getBottomRightBorder());
        viewportRect.setDimension(tl, br);
        viewportRect.draw();
    }
}
