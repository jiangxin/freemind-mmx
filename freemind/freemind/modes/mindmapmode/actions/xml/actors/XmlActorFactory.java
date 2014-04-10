/*FreeMind - A Program for creating and viewing Mindmaps
*Copyright (C) 2000-2014 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
*
*See COPYING for Details
*
*This program is free software; you can redistribute it and/or
*modify it under the terms of the GNU General Public License
*as published by the Free Software Foundation; either version 2
*of the License, or (at your option) any later version.
*
*This program is distributed in the hope that it will be useful,
*but WITHOUT ANY WARRANTY; without even the implied warranty of
*MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*GNU General Public License for more details.
*
*You should have received a copy of the GNU General Public License
*along with this program; if not, write to the Free Software
*Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/

package freemind.modes.mindmapmode.actions.xml.actors;

import freemind.modes.ExtendedMapFeedback;

/**
 * Creates all XmlActors needed for the MindMapController
 * 
 * @author foltin
 * @date 16.03.2014
 */
public class XmlActorFactory {

	private ExtendedMapFeedback mMapFeedback;
	private ItalicNodeActor mActionActor;
	private BoldNodeActor mBoldActor;
	private NewChildActor mNewChildActor;
	private DeleteChildActor mDeleteChildActor;
	private PasteActor mPasteActor;
	private RemoveAllIconsActor mRemoveAllIconsActor;
	private AddIconActor mAddIconActor;
	private RemoveIconActor mRemoveIconActor;
	private CloudActor mCloudActor;
	private EdgeStyleActor mEdgeStyleActor;
	private EdgeWidthActor mEdgeWidthActor;
	private FontFamilyActor mFontFamilyActor;
	private FontSizeActor mFontSizeActor;
	private MoveNodeActor mMoveNodeActor;
	private NodeStyleActor mNodeStyleActor;
	private UnderlineActor mUnderlineActor;
	private AddArrowLinkActor mAddArrowLinkActor;
	private RemoveArrowLinkActor mRemoveArrowLinkActor;
	private ChangeArrowLinkEndPointsActor mChangeArrowLinkEndPointsActor;
	private ChangeArrowsInArrowLinkActor mChangeArrowsInArrowLinkActor;
	private CloudColorActor mCloudColorActor;
	private ColorArrowLinkActor mColorArrowLinkActor;
	private EdgeColorActor mEdgeColorActor;
	private EditActor mEditActor;
	private NodeBackgroundColorActor mNodeBackgroundColorActor;
	private NodeColorActor mNodeColorActor;
	private AddHookActor mAddHookActor;
	private NodeUpActor mNodeUpActor;
	private RevertActor mRevertActor;
	private ToggleFoldedActor mToggleFoldedActor;
	private SetLinkActor mSetLinkActor;

	public XmlActorFactory(ExtendedMapFeedback pMapFeedback) {
		mMapFeedback = pMapFeedback;
		mActionActor = new ItalicNodeActor(mMapFeedback);
		mBoldActor = new BoldNodeActor(mMapFeedback);
		mNewChildActor = new NewChildActor(mMapFeedback);
		mDeleteChildActor = new DeleteChildActor(mMapFeedback);
		mPasteActor = new PasteActor(mMapFeedback);
		mRemoveAllIconsActor = new RemoveAllIconsActor(mMapFeedback);
		mAddIconActor = new AddIconActor(mMapFeedback);
		mRemoveIconActor = new RemoveIconActor(mMapFeedback);
		mCloudActor = new CloudActor(mMapFeedback);
		mEdgeStyleActor = new EdgeStyleActor(mMapFeedback);
		mEdgeWidthActor = new EdgeWidthActor(mMapFeedback);
		mFontFamilyActor = new FontFamilyActor(mMapFeedback);
		mFontSizeActor = new FontSizeActor(mMapFeedback);
		mMoveNodeActor = new MoveNodeActor(mMapFeedback);
		mNodeStyleActor = new NodeStyleActor(mMapFeedback);
		mUnderlineActor = new UnderlineActor(mMapFeedback);
		mAddArrowLinkActor = new AddArrowLinkActor(mMapFeedback);
		mRemoveArrowLinkActor = new RemoveArrowLinkActor(mMapFeedback);
		mChangeArrowLinkEndPointsActor = new ChangeArrowLinkEndPointsActor(mMapFeedback);
		mChangeArrowsInArrowLinkActor = new ChangeArrowsInArrowLinkActor(mMapFeedback);
		mCloudColorActor = new CloudColorActor(mMapFeedback);
		mColorArrowLinkActor = new ColorArrowLinkActor(mMapFeedback);
		mEdgeColorActor = new EdgeColorActor(mMapFeedback);
		mEditActor = new EditActor(mMapFeedback);
		mNodeBackgroundColorActor = new NodeBackgroundColorActor(mMapFeedback);
		mNodeColorActor = new NodeColorActor(mMapFeedback);
		mAddHookActor = new AddHookActor(mMapFeedback);
		mNodeUpActor = new NodeUpActor(mMapFeedback);
		mRevertActor = new RevertActor(mMapFeedback);
		mToggleFoldedActor = new ToggleFoldedActor(mMapFeedback);
		mSetLinkActor = new SetLinkActor(mMapFeedback);
	}
	
	public ItalicNodeActor getItalicActor() {
		return mActionActor;
	}
	
	public BoldNodeActor getBoldActor() {
		return mBoldActor;
	}

	public NewChildActor getNewChildActor() {
		return mNewChildActor;
	}
	
	public DeleteChildActor getDeleteChildActor() {
		return mDeleteChildActor;
	}
	
	public PasteActor getPasteActor() {
		return mPasteActor;
	}
	
	public RemoveAllIconsActor getRemoveAllIconsActor() {
		return mRemoveAllIconsActor;
	}
	
	public AddIconActor getAddIconActor() {
		return mAddIconActor;
	}
	
	public RemoveIconActor getRemoveIconActor() {
		return mRemoveIconActor;
	}
	
	public CloudActor getCloudActor() {
		return mCloudActor;
	}

	public EdgeStyleActor getEdgeStyleActor() {
		return mEdgeStyleActor;
	}
	
	public EdgeWidthActor getEdgeWidthActor() {
		return mEdgeWidthActor;
	}
	
	public FontFamilyActor getFontFamilyActor() {
		return mFontFamilyActor;
	}

	public FontSizeActor getFontSizeActor() {
		return mFontSizeActor;
	}
	
	public MoveNodeActor getMoveNodeActor() {
		return mMoveNodeActor;
	}
	
	public NodeStyleActor getNodeStyleActor() {
		return mNodeStyleActor;
	}
	
	public UnderlineActor getUnderlineActor() {
		return mUnderlineActor;
	}

	public AddArrowLinkActor getAddArrowLinkActor() {
		return mAddArrowLinkActor;
	}
	
	public RemoveArrowLinkActor getRemoveArrowLinkActor() {
		return mRemoveArrowLinkActor;
	}
	
	public ChangeArrowLinkEndPointsActor getChangeArrowLinkEndPointsActor() {
		return mChangeArrowLinkEndPointsActor;
	}
	
	public ChangeArrowsInArrowLinkActor getChangeArrowsInArrowLinkActor() {
		return mChangeArrowsInArrowLinkActor;
	}
	
	public CloudColorActor getCloudColorActor() {
		return mCloudColorActor;
	}
	
	public ColorArrowLinkActor getColorArrowLinkActor() {
		return mColorArrowLinkActor;
	}
	
	public EdgeColorActor getEdgeColorActor() {
		return mEdgeColorActor;
	}
	
	public EditActor getEditActor() {
		return mEditActor;
	}
	
	public NodeBackgroundColorActor getNodeBackgroundColorActor() {
		return mNodeBackgroundColorActor;
	}
	
	public NodeColorActor getNodeColorActor() {
		return mNodeColorActor;
	}
	
	public AddHookActor getAddHookActor() {
		return mAddHookActor;
	}

	public NodeUpActor getNodeUpActor() {
		return mNodeUpActor;
	}
	
	public RevertActor getRevertActor() {
		return mRevertActor;
	}
	
	public ToggleFoldedActor getToggleFoldedActor() {
		return mToggleFoldedActor;
	}
	
	public SetLinkActor getSetLinkActor() {
		return mSetLinkActor;
	}
	
}
