package opencsp.csta;

import opencsp.csta.xml.CSTAXmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The EventCause parameter type provides additional information on why an event was generated.
 * @see "ECMA-269 - 12.2.15 - EventCause"
 * @see "http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-269.pdf"
 * @see "ECMA-323 - 9.18 - Event Cause"
 * @see "http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-323.pdf"
 */
public enum EventCause implements CSTAXmlSerializable {
    ACDBusy("aCDBusy"),
    ACDForward("aCDForward"),
    ACDSaturated("aCDSaturated"),
    Activation("activation"),
    ActiveParticipation("activeParticipation"),
    AlertTimeExpired("alertTimeExpired"),
    Alternate("alternate"),
    AutoWork("autoWork"),
    Babble("babble"),
    BadAttribute("badAttribute"),
    BadGrammar("badGrammar"),
    BadURI("badURI"),
    BadVoice("badVoice"),
    Blocked("blocked"),
    Busy("busy"),
    BusyOverflow("busyOverflow"),
    CalendarOverflow("calendarOverflow"),
    CallBack("callBack"),
    CallCancelled("callCancelled"),
    CallForward("callForward"),
    CallForwardImmediate("callForwardImmediate"),
    CallForwardBusy("callForwardBusy"),
    CallForwardNoAnswer("callForwardNoAnswer"),
    CallInterception("callInterception"),
    CallInterceptionBusy("callInterceptionBusy"),
    CallInterceptionForwarded("callInterceptionForwarded"),
    CallInterceptionNoAnswer("callInterceptionNoAnswer"),
    CallInterceptionResourcesNotAvailable("callInterceptionResourcesNotAvailable"),
    CallNotAnswered("callNotAnswered"),
    CallPickup("callPickup"),
    CampOn("campOn"),
    CapOnTrunks("campOnTrunks"),
    CapacityOverflow("capacityOverflow"),
    CharacterCountReached("characterCountReached"),
    Conference("conference"),
    Consultation("consultation"),
    DestDetected("destDetected"),
    DestNotObtainable("destNotObtainable"),
    DestOutOfOrder("destOutOfOrder"),
    Distributed("distributed"),
    DistributionDelay("distributionDelay"),
    DoNotDisturb("doNotDisturb"),
    DTMFDigitDetected("dTMFDigitDetected"),
    DuplicateDTMF("duplicateDTMF"),
    DurationExceeded("durationExceeded"),
    EarlyStop("earlyStop"),
    EmptyQueue("emptyQueue"),
    EndOfMessageDetected("endOfMessageDetected"),
    EnteringDistribution("enteringDistribution"),
    ForcedPause("forcedPause"),
    ForcedTransition("forcedTransition"),
    IncompatibleDestination("incompatibleDestination"),
    InterdigitTimeout("interdigitTimeout"),
    Intrude("intrude"),
    InvalidAccountCode("invalidAccountCode"),
    InvalidConnection("invalidConnection"),
    InvalidConnectionState("invalidConnectionState"),
    InvalidNumberFormat("invalidNumberFormat"),
    JoinCall("joinCall"),
    KeyOperation("keyOperation"),
    KeyOperationInUse("keyOperationInUse"),
    LawfulInterception("lawfulInterception"),
    Lockout("lockout"),
    Maintenance("maintenance"),
    MakeCall("makeCall"),
    MakeConnection("makeConnection"),
    MakePredictiveCall("makePredictiveCall"),
    MaxTimeout("maxTimeout"),
    MessageDurationExceeded("messageDurationExceeded"),
    MessageSizeExceeded("messageSizeExceeded"),
    MultipleAlerting("multipleAlerting"),
    MultipleQueuing("multipleQueuing"),
    NetworkCongestion("networkCongestion"),
    NetworkDialling("networkDialling"),
    NetworkNotObtainable("networkNotObtainable"),
    NetworkOutOfOrder("networkOutOfOrder"),
    NetworkSignal("networkSignal"),
    NewCall("newCall"),
    NextMessage("nextMessage"),
    NoAvailableAgents("noAvailableAgents"),
    Normal("normal"),
    NormalClearing("normalClearing"),
    NoAudioSaved("noAudioSaved"),
    NoQueue("noQueue"),
    NoRule("noRule"),
    NoSpeechDetected("noSpeechDetected"),
    NoAvailableBearerService("noAvailableBearerService"),
    NotSupportedBearerService("notSupportedBeaererService"),
    NumberChanged("numberChanged"),
    NumberUnallocated("numberUnallocated"),
    OutOfGrammar("outOfGrammar"),
    Overflow("overflow"),
    Override("override"),
    Park("park"),
    PathReplacement("pathReplacement"),
    QueueCleared("queueCleared"),
    QueueTimeOverflow("queueTimeOverflow"),
    Recall("recall"),
    RecallBusy("recallBusy"),
    RecallForwarded("recallForwarded"),
    RecallNoAnswer("recallNoAnswer"),
    RecallResourcesNotAvailable("recallResourcesNotAvailable"),
    Redirected("redirected"),
    RemainsInQueue("remainsInQueue"),
    ReorderTone("reorderTone"),
    Reserved("reserved"),
    ResourcesNotAvailable("resourcesNotAvailable"),
    SelectedTrunkBusy("selectedTrunkBusy"),
    SilentParticipation("silentParticipation"),
    SingleStepConference("singleStepConference"),
    SingleStepTransfer("singleStepTransfer"),
    SpeechDetected("speechDetected"),
    Suspend("suspend"),
    SwitchingFunctionTerminated("switchingFunctionTerminated"),
    TerminationCharacterReceived("terminationCharacterReceived"),
    Timeout("timeout"),
    Transfer("transfer"),
    TrunksBusy("trunksBusy"),
    UnauthorisedBearerService("unauthorizedBeaererService"),
    UnknownOverflow("unknownOverflow");

    private final String cause;

    EventCause(String s) {
        cause = s;
    }

    public boolean equalsCause(String otherCause) {
        return otherCause != null && cause.equals(otherCause);
    }

    public String toString() {
        return this.cause;
    }

    public Element toXmlElement(Document doc, String tagName) {
        Element e = doc.createElement(tagName);
        e.setTextContent(cause);
        return e;
    }

    public Element toXmlElement(Document doc) {
        return toXmlElement(doc, "cause");
    }
}
