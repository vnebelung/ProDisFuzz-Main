/*
 * This file is part of ProDisFuzz, modified on 25.07.15 21:43.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.monitor.protocol;

import model.process.monitor.protocol.StateMachine.Command;
import model.process.monitor.protocol.StateMachine.StateType;

import java.util.HashMap;
import java.util.Map;

public class State {

    private Map<Command, StateType> transitions;

    /**
     * Instantiates a new protocol state.
     */
    public State() {
        transitions = new HashMap<>(4);
    }

    /**
     * Returns the state that is the active one after calling the given command. If the given command is not allowed to
     * be called at this state, an Exception will be thrown.
     *
     * @param command the command that is going to be called
     * @return the new protocol state
     * @throws IllegalArgumentException if the given command is not allowed to be called at this state
     */
    public StateType getNextStateFor(Command command) throws IllegalArgumentException {
        if (!transitions.containsKey(command)) {
            throw new IllegalArgumentException();
        }
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
