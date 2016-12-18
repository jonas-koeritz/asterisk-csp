package opencsp.util;

import java.util.ArrayList;
import java.util.List;

public interface IStateMachine {
    public State getCurrentState();

    public default boolean transitionPossible(State targetState)
    {
        return transitionPossible(getCurrentState(), targetState);
    }

    public boolean transitionPossible(State from, State to);

    public default List<State> getPossibleStateTransitions()
    {
        List<State> possibleStateTransitions = new ArrayList<>();
        for(State s : getAllStates())
        {
            if(transitionPossible(getCurrentState(), s))
            {
                possibleStateTransitions.add(s);
            }
        }
        return possibleStateTransitions;
    }


    public List<State> getAllStates();
}
