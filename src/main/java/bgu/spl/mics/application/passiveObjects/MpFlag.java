package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

/**
 * Enum to differentiate getter and sender Money Penny
 */
public enum MpFlag implements Serializable {
    GETTING_AGENTS, SENDING_AGENTS, RELEASING_AGENTS
}
