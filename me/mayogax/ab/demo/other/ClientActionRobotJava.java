/*****************************************************************************
 ** ANGRYBIRDS AI AGENT FRAMEWORK
 ** Copyright (c) 2014, XiaoYu (Gary) Ge, Stephen Gould, Jochen Renz
 **  Sahan Abeyasinghe,Jim Keys,  Andrew Wang, Peng Zhang
 ** All rights reserved.
**This work is licensed under the terms of the GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
**To view a copy of this license, visit http://www.gnu.org/licenses/
 *****************************************************************************/

package me.mayogax.ab.demo.other;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import me.mayogax.ab.vision.ABObject;
import me.mayogax.ab.vision.ABType;
import me.mayogax.ab.vision.Vision;
import me.mayogax.ab.vision.GameStateExtractor.GameState;
import me.mayogax.external.ClientMessageEncoder;

//Java interface of ClientActionRobot
public class ClientActionRobotJava extends ClientActionRobot {

	public ClientActionRobotJava(String ip) {
		super(ip);
	}
   
	//return game state as enum format
	public GameState checkState() {
		byte result = super.getState();
		GameState state = GameState.values()[result];
		return state;
	}
	//return an array of best scores. the nth slot stores the score of (n+1)th level
	public int[] checkScore()
	{
		byte[] scores = super.getBestScores();
		int[] _scores = new int[scores.length/4];
		for (int i = 0 ; i < _scores.length ; i++){
			_scores[i] = super.bytesToInt(scores[i * 4], scores[i*4 + 1], scores[i*4 + 2], scores[i*4 + 3]);
		}
		return _scores;
	}
	
	//send a shot message using int values as input
	public byte[] shoot(int fx, int fy, int dx, int dy, int t1, int t2,
			boolean polar) {
		return super.shoot(intToByteArray(fx), intToByteArray(fy),
				intToByteArray(dx), intToByteArray(dy), intToByteArray(t1),
				intToByteArray(t2), polar);
	}

	//send a shot sequence message using int arrays as input
	//one array per shot
	public byte[] cshootSequence(int[]... shots) {
		byte[][] byteShots = new byte[shots.length][24] ;
		int shotCount = 0;
		for (int[] shot : shots) {
			byteShots[shotCount] = ClientMessageEncoder.mergeArray(intToByteArray(shot[0])/*fx*/,
					intToByteArray(shot[1])/*fy*/, intToByteArray(shot[2])/*dx*/,
					intToByteArray(shot[3])/*dy*/, intToByteArray(shot[4])/*t1*/,
					intToByteArray(shot[5])/*t2*/);
			shotCount++;
		}
		return super.cshootSequence(byteShots);
	}
	public ABType getBirdTypeOnSling()
	{
		fullyZoomIn();
		BufferedImage screenshot = doScreenShot();
		Vision vision = new Vision(screenshot);
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			
			e.printStackTrace();
		}
		fullyZoomOut();
		List<ABObject> _birds = vision.findBirdsMBR();
		if(_birds.isEmpty())
			return ABType.Unknown;
		Collections.sort(_birds, new Comparator<Rectangle>(){

			@Override
			public int compare(Rectangle o1, Rectangle o2) {
				
				return ((Integer)(o1.y)).compareTo((Integer)(o2.y));
			}	
		});
		return _birds.get(0).getType();
	}
	public int[] checkMyScore() {
		byte[] scores = super.getMyScore();
		int[] _scores = new int[scores.length/4];
		for (int i = 0 ; i < _scores.length ; i++){
			_scores[i] = super.bytesToInt(scores[i * 4], scores[i*4 + 1], scores[i*4 + 2], scores[i*4 + 3]);
		}
		return _scores;
	}
}
