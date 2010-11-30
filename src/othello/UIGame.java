package othello;

import core.SquareState;
import utils.Transform;
import logic.Controller;
import ui.BoardUI;
import ui.ImageComponent;
import ui.DiskComponentFactory.DiskCompType;
import java.util.Set;
import java.awt.Point;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class UIGame implements Runnable {

	private Controller controller = Controller.getInstance();
	private BoardUI boardUI;
	private Set<Point> possblMoves;

	public UIGame() {
		this.controller.init();
		initBoardUI();
	}

	private void initBoardUI() {
		boardUI = new BoardUI();
		boardUI.setVisible(true);
	}

	@Override
	synchronized public void run() {
		if (controller.endOfGame()) {
			gameEnd();
		} else {
			possblMoves = markPossibleMoves();
			if (possblMoves.isEmpty()) {
				pass();
				run();
			}
		}
	}

	private Set<Point> markPossibleMoves() {
		Set<Point> moves = controller.markPossibleMoves();
		controller.unmarkPossibleMoves();
		if (!moves.isEmpty()) {
			DiskCompType color = controller.who().color() == SquareState.WHITE
					     ? DiskCompType.PSSBLWHT : DiskCompType.PSSBLBLK;
			boardUI.markPossibleMoves(moves, color);
		}
		updateListeners();
		return moves;
	}

	private void updateStats() {
		boardUI.updateScore(controller.getBlackScore(), controller.getWhiteScore());
	}

	private void changeTurn() {
		controller.changeTurn();
		boardUI.updateTurn(controller.who().toString());
	}

	private void lostTurn() {
		boardUI.notifyLostTurn();
		changeTurn();
	}

	private void pass() {
		lostTurn();
		updateStats();
	}

	private void updateListeners() {
		for (ImageComponent imgComp : boardUI.getDiskComps()) {
			if (imgComp.getMouseListeners().length != 0) {
				continue;
			}
			imgComp.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(MouseEvent evt) {
					clickzWasHappend(evt.getComponent());
				}
			});
		}
	}

	private void clickzWasHappend(Component imgComp) {
		int index = boardUI.getDiskComps().indexOf(imgComp);
		Point selectedMove = Transform.indexToPoint(index);
		if (possblMoves.contains(selectedMove)) {
			boardUI.unmarkPossibleMoves(possblMoves);
			makeMove(selectedMove);
			updateStats();
			changeTurn();
			run();
		}
	}

	private void makeMove(Point move) {
		DiskCompType color = controller.who().color() == SquareState.WHITE
				     ? DiskCompType.WHITE : DiskCompType.BLACK;
		Set<Point> disksToChange = controller.makeMove(move);
		boardUI.fill(disksToChange, color);
	}

	private void gameEnd() {
		updateStats();
		if (controller.isDraw()) {
			boardUI.declareDraw();
		} else {
			boardUI.declareWinner(controller.getWinner().toString());
		}
	}

        synchronized public boolean rematch() {
            boolean answer = false;
            /* read answer and return it */
            return answer;
        }
}
