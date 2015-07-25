/*
 * This file is part of ProDisFuzz, modified on 25.07.15 21:43.
 * Copyright (c) 2013-2015 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.process.monitor.protocol;

import java.util.HashMap;
import java.util.Map;

public class StateMachine {

    private StateType currentStateType;
    private Map<StateType, State> states;

    /**
     * Instantiates a new state machine which controls the protocol used to connect a monitor server and client.
     */
    public StateMachine() {
        states = new HashMap<>(4);

        State state = new State();
        state.addTransition(Command.AYT, StateType.CONNECTED);
        state.addTransition(Command.RST, StateType.NEW);
        states.put(StateType.NEW, state);

        state = new State();
        state.addTransition(Command.SFP, StateType.CONFIGURED);
        state.addTransition(Command.RST, StateType.NEW);
        states.put(StateType.CONNECTED, state);

        state = new State();
        state.addTransition(Command.CTD, StateType.FUZZING);
        state.addTransition(Command.SFP, StateType.CONFIGURED);
        state.addTransition(Command.GFP, StateType.CONFIGURED);
        state.addTransition(Command.RST, StateType.NEW);
        states.put(StateType.CONFIGURED, state);

        state = new State();
        state.addTransition(Command.CTD, StateType.FUZZING);
        state.addTransition(Command.RST, StateType.NEW);
        states.put(StateType.FUZZING, state);

        currentStateType = StateType.NEW;
    }

    /**
     * Updates the state of the connection by indicating that the given command is going to be called. To successfully
     * update the protocol state the given command must be allowed to be called at the current state, otherwise an
     * exception is thrown.
     *
     * @param command the command that is going to be called at caller's side
     * @throws IllegalStateException if the given command is not allowed to be executed at the current state
     */
    public void updateWith(Command command) throws IllegalStateException {
        try {
            currentStateType = states.get(currentStateType).getNextStateFor(command);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Command '" + command + "' not allowed in state '" + currentStateType
                    + "'");
        }
    }

    /**
     * Checks whether the given command is allowed at the current state.
     *
     * @param command the command to check
     * @return true, if the command is allowed at the current state
     */
    public boolean isAllowedAtCurrentState(Command command) {
        try {
            states.get(currentStateType).getNextStateFor(command);
            return true;
        } catch (IllegalArgumentException ignored) {
            return false;
        }
    }

    public enum StateType {NEW, CONNECTED, CONFIGURED, FUZZING}

    public enum Command {AYT, GFP, SFP, CTD, RST}
}
