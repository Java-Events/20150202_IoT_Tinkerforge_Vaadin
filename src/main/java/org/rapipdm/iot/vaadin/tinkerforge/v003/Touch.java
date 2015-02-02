package org.rapipdm.iot.vaadin.tinkerforge.v003;

import com.tinkerforge.BrickletMultiTouch;
import com.tinkerforge.IPConnection;

/**
 * Created by sven on 28.01.15.
 */
public class Touch {

    private BrickletMultiTouch multiTouch;

    public Touch(final String UID, IPConnection ipConnection) {
        this.multiTouch = new BrickletMultiTouch(UID, ipConnection);
    }





}
