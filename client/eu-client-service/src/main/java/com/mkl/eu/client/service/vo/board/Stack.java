package com.mkl.eu.client.service.vo.board;

import com.mkl.eu.client.service.vo.EuObject;
import com.mkl.eu.client.service.vo.enumeration.MovePhaseEnum;

import javax.xml.bind.Unmarshaller;
import java.util.ArrayList;
import java.util.List;

/**
 * Stack of counters (regroupment).
 *
 * @author MKL
 */
public class Stack extends EuObject {
    /** Province where the stack is located. */
    private String province;
    /** Phase of the move the stack is (has moved, is moving,..). */
    private MovePhaseEnum movePhase;
    /** Flag saying that the stack is being besieged. */
    private boolean besieged;
    /** Country managing the stack. */
    private String country;
    /** Number of movement points already done by this stack. */
    private int move;
    /** Counters of the stack. */
    private List<Counter> counters = new ArrayList<>();

    /** @return the province. */
    public String getProvince() {
        return province;
    }

    /** @param province the province to set. */
    public void setProvince(String province) {
        this.province = province;
    }

    /** @return the movePhase. */
    public MovePhaseEnum getMovePhase() {
        return movePhase;
    }

    /** @param movePhase the movePhase to set. */
    public void setMovePhase(MovePhaseEnum movePhase) {
        this.movePhase = movePhase;
    }

    /** @return the besieged. */
    public boolean isBesieged() {
        return besieged;
    }

    /** @param besieged the besieged to set. */
    public void setBesieged(boolean besieged) {
        this.besieged = besieged;
    }

    /** @return the country. */
    public String getCountry() {
        return country;
    }

    /** @param country the country to set. */
    public void setCountry(String country) {
        this.country = country;
    }

    /** @return the move. */
    public int getMove() {
        return move;
    }

    /** @param move the move to set. */
    public void setMove(int move) {
        this.move = move;
    }

    /** @return the counters. */
    public List<Counter> getCounters() {
        return counters;
    }

    /** @param counters the counters to set. */
    public void setCounters(List<Counter> counters) {
        this.counters = counters;
    }

    /**
     * This method is called after all the properties (except IDREF) are unmarshalled for this object,
     * but before this object is set to the parent object.
     *
     * @param unmarshaller the unmarshaller.
     * @param parent       the parent object.
     */
    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        if (this.counters != null) {
            for (Counter counter : counters) {
                counter.setOwner(this);
            }
        }
    }
}
