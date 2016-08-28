/*
 * This file is part of ProDisFuzz, modified on 28.08.16 20:30.
 * Copyright (c) 2013-2016 Volker Nebelung <vnebelung@prodisfuzz.net>
 * This work is free. You can redistribute it and/or modify it under the
 * terms of the Do What The Fuck You Want To Public License, Version 2,
 * as published by Sam Hocevar. See the COPYING file for more details.
 */

package model.monitor.protocol;

import model.monitor.message.TransmitMessage.Command;

import java.util.EnumMap;
import java.util.Map;

/**
 * This class represents the state machine used to track the state of the protocol used to communicate with the monitor
 * component.
 */
class StateMachine {

    private StateType currentStateType;
    private Map<StateType, State> states;

    /**
     * Constructs a new state machine which controls the protocol used to connect a monitor server and client.
     */
    public StateMachine() {
        states = new EnumMap<>(StateType.class);

        State state = new State();
        state.addTransition(Command.AYT, StateType.MONITOR_SET);
        states.put(StateType.NEW, state);

        state = new State();
        state.addTransition(Command.SCO, StateType.CONNECTOR_SET);
        state.addTransition(Command.RST, StateType.NEW);
        state.addTransition(Command.GCO, StateType.MONITOR_SET);
        states.put(StateType.MONITOR_SET, state);

        state = new State();
        state.addTransition(Command.SCP, StateType.CONNECTOR_SET);
        state.addTransition(Command.SCO, StateType.CONNECTOR_SET);
        state.addTransition(Command.CTT, StateType.CONNECTOR_READY);
        state.addTransition(Command.RST, StateType.NEW);
        states.put(StateType.CONNECTOR_SET, state);

        state = new State();
        state.addTransition(Command.SCP, StateType.CONNECTOR_SET);
        state.addTransition(Command.SCO, StateType.CONNECTOR_SET);
        state.addTransition(Command.SWA, StateType.WATCHER_SET);
        state.addTransition(Command.GWA, StateType.CONNECTOR_READY);
        state.addTransition(Command.RST, StateType.NEW);
        states.put(StateType.CONNECTOR_READY, state);

        state = new State();
        state.addTransition(Command.SWA, StateType.WATCHER_SET);
        state.addTransition(Command.CTF, StateType.FUZZING);
        state.addTransition(Command.RST, StateType.NEW);
        states.put(StateType.WATCHER_SET, state);

        state = new State();
        state.addTransition(Command.CTF, StateType.FUZZING);
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
     * @throws ProtocolStateException if the given command is not allowed to be executed at the current protocol state
     */
    public void updateWith(Command command) throws ProtocolStateException {
        StateType stateType = states.get(currentStateType).getNextStateFor(command);
        if (stateType == null) {
            throw new ProtocolStateException(
                    "Protocol error: Command '" + command + "' not allowed in state '" + currentStateType + '\'');
        }
        currentStateType = stateType;
    }

    /**
     * Checks whether the given command is allowed at the current state.
     *
     * @param command the command to check
     * @return true, if the command is allowed at the current state
     */
    public boolean isAllowedAtCurrentState(Command command) {
        return states.get(currentStateType).getNextStateFor(command) != null;
    }

    public enum StateType {NEW, MONITOR_SET, CONNECTOR_SET, CONNECTOR_READY, WATCHER_SET, FUZZING}

}
