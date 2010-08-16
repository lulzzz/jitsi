/*
 * SIP Communicator, the OpenSource Java VoIP and Instant Messaging client.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package net.java.sip.communicator.impl.protocol.jabber;

import net.java.sip.communicator.service.protocol.*;
import net.java.sip.communicator.service.protocol.media.*;
import net.java.sip.communicator.util.*;

/**
 * Implements <tt>OperationSetVideoTelephony</tt> in order to give access to
 * video-specific functionality in the Jabber protocol implementation.
 *
 * @author Emil Ivov
 */
public class OperationSetVideoTelephonyJabberImpl
    extends AbstractOperationSetVideoTelephony<
        OperationSetBasicTelephonyJabberImpl,
        ProtocolProviderServiceJabberImpl,
        CallJabberImpl,
        CallPeerJabberImpl>

{
    /**
     * The <tt>Logger</tt> used by the
     * <tt>OperationSetTelephonyConferencingJabberImpl</tt> class and its
     * instances for logging output.
     */
    private static final Logger logger
        = Logger.getLogger(OperationSetVideoTelephonyJabberImpl.class);


    /**
     * Initializes a new <tt>OperationSetVideoTelephonyJabberImpl</tt> instance
     * which builds upon the telephony-related functionality of a specific
     * <tt>OperationSetBasicTelephonyJabberImpl</tt>.
     *
     * @param basicTelephony the <tt>OperationSetBasicTelephonyJabberImpl</tt>
     *            the new extension should build upon
     */
    public OperationSetVideoTelephonyJabberImpl(
        OperationSetBasicTelephonyJabberImpl basicTelephony)
    {
        super(basicTelephony);
    }

    /**
     * Implements OperationSetVideoTelephony#setLocalVideoAllowed(Call,
     * boolean). Modifies the local media setup to reflect the requested setting
     * for the streaming of the local video and then re-invites all
     * CallPeers to re-negotiate the modified media setup.
     *
     * @param call the call where we'd like to allow sending local video.
     * @param allowed <tt>true</tt> if local video transmission is allowed and
     * <tt>false</tt> otherwise.
     *
     *  @throws OperationFailedException if video initialization fails.
     */
    @Override
    public void setLocalVideoAllowed(Call call, boolean allowed)
        throws OperationFailedException
    {
        super.setLocalVideoAllowed(call, allowed);

        ((CallJabberImpl)call).modifyVideoContent(allowed);
    }

    /**
     * Create a new video call and invite the specified CallPeer to it.
     *
     * @param uri the address of the callee that we should invite to a new
     * call.
     * @return CallPeer the CallPeer that will represented by the
     * specified uri. All following state change events will be delivered
     * through that call peer. The Call that this peer is a member
     * of could be retrieved from the CallParticipatn instance with the use
     * of the corresponding method.
     * @throws OperationFailedException with the corresponding code if we fail
     * to create the video call.
     */
    public Call createVideoCall(String uri)
        throws OperationFailedException
    {
        return createOutgoingVideoCall(uri);
    }

    /**
     * Create a new video call and invite the specified CallPeer to it.
     *
     * @param callee the address of the callee that we should invite to a new
     * call.
     * @return CallPeer the CallPeer that will represented by the
     * specified uri. All following state change events will be delivered
     * through that call peer. The Call that this peer is a member
     * of could be retrieved from the CallParticipatn instance with the use
     * of the corresponding method.
     * @throws OperationFailedException with the corresponding code if we fail
     * to create the video call.
     */
    public Call createVideoCall(Contact callee) throws OperationFailedException
    {
        return createOutgoingVideoCall(callee.getAddress());
    }

    /**
     * Check if the remote part supports Jingle video.
     *
     * @param calleeAddress Contact address
     * @return true if contact support Jingle video, false otherwise
     *
     * @exception OperationFailedException with the corresponding code if we fail
     * to create the video call.
     */
    private Call createOutgoingVideoCall(String calleeAddress)
        throws OperationFailedException
    {
        if (logger.isInfoEnabled())
            logger.info("creating outgoing video call...");

        if (parentProvider.getConnection() == null)
        {
            throw new OperationFailedException(
                    "Failed to create OutgoingJingleSession.\n"
                    + "we don't have a valid XMPPConnection."
                    , OperationFailedException.INTERNAL_ERROR);
        }

        CallJabberImpl call = new CallJabberImpl(basicTelephony);
        /* enable video */
        call.setLocalVideoAllowed(true, getMediaUseCase());

        return basicTelephony.createOutgoingCall(call, calleeAddress);
    }

    /**
     * Indicates a user request to answer an incoming call with video enabled
     * from the specified CallPeer.
     *
     * @param peer the call peer that we'd like to answer.
     * @throws OperationFailedException with the corresponding code if we
     * encounter an error while performing this operation.
     */
    public void answerVideoCallPeer(CallPeer peer)
        throws OperationFailedException
    {
        CallPeerJabberImpl callPeer = (CallPeerJabberImpl) peer;

        /* answer with video */
        callPeer.getCall().setLocalVideoAllowed(true, getMediaUseCase());
        callPeer.answer();
    }
}
