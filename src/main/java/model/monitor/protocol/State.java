/*
 * This file is part of ProDisFuzz, modified on 28.08.16 19:39.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.monitor.protocol;

import model.monitor.message.TransmitMessage.Command;
import model.monitor.protocol.StateMachine.StateType;

import java.util.EnumMap;
import java.util.Map;

/**
 * This class represents a state in the protocol used for communication between main component and monitor.
 */
class State {

    private Map<Command, StateType> transitions;

    /**
     * Constructs a new protocol state.
     */
    public State() {
        transitions = new EnumMap<>(Command.class);
    }

    /**
     * Returns the state that is the active one after calling the given command.
     *
     * @param command the command that is going to be called
     * @return the new protocol state or null, if the command is not allowed to be called at this state
     */
    public StateType getNextStateFor(Command command) {
        return transitions.get(command);
    }

    /**
     * Sets the given pairs of commands and states as the allowed transitions of this state. A command/state pair
     * indicates that the given command shall be allowed in this protocol state and leads to its given state.
     *
     * @param command   the command that is allowed
     * @param stateType the new state the protocol will have after calling the given command
     */
    public void addTransition(Command command, StateType stateType) {
        transitions.put(command, stateType);
    }
}
