import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.io.File;
import java.io.IOException;

public class Sudoku {
	
	Scanner input;
	Scanner reader;
	Variable[][] solve;
	ArrayList<Variable> empty;
	Stack<Variable> changes;
	
	Sudoku() throws IOException{
		input = new Scanner(System.in);
		System.out.println("Enter file name:");
		reader = new Scanner(new File(input.nextLine()));
		input.close();
		solve = new Variable[9][9];
		empty = new ArrayList<>();
		changes = new Stack<Variable>();
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				solve[i][j] = new Variable(reader.nextInt(),i,j);
			}
		}
		reader.close();
		
		for(int i = 0; i < 9; i++) { // constraint propagation
			for(int j = 0; j < 9; j++) {
				if(solve[i][j].getValue() != 0) {
					this.propagate(solve[i][j]);
				}
			}
		}
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				if(solve[i][j].getValue() == 0) {
					empty.add(solve[i][j]);
				}
			}
		}
		sortEmpty(); // puts variables in order of least number of choices
		
		Variable var = empty.get(0);
		int x = var.getX();
		int y = var.getY();
		backtrack(var, 0); // recursive backtracking
		
		this.printSolve();
	}
	
	private class Variable{
		
		private int value;
		private ArrayList<Integer> originalChoices;
		private ArrayList<Integer> choices;
		private int x;
		private int y;
		
		private Variable(int val, int inx, int iny){
			value = val;
			x = inx;
			y = iny;
			originalChoices = new ArrayList<>();
			if(value == 0) {
				for (int i = 1; i <= 9; i++) {
					originalChoices.add(i);
				}
			}
			choices = new ArrayList<>();
			for(int choice: originalChoices) {
				choices.add(choice);
			}
		}
		
		private int getX() {
			return x;
		}
		
		private int getY() {
			return y;
		}
		
		private int getValue(){
			return value;
		}
		
		private void setValue(int val){
			value = val;
		}
		
		private int numChoices() {
			return choices.size();
		}
		
		private ArrayList<Integer> getChoices() {
			return choices;
		}
		
		private boolean containsChoice(int choice) {
			if(choices.indexOf(choices) == -1) {
				return false;
			} else {
				return true;
			}
		}
		
		private void removeChoice(int choice) {
			if(choices.indexOf(choice) != -1) {
				choices.remove(choices.indexOf(choice));
			}
		}
		
	}
	
	public void propagate(Variable var) {
		int value = var.getValue();
		int x = var.getX();
		int y = var.getY();
		for(int i = 0; i < 9; i++) { // removes choices from rows + columns
			if(i != y) {
				solve[x][i].removeChoice(value);
			}
			if(i != x) {
				solve[i][y].removeChoice(value);
			}
		}
		int brow = (x / 3) * 3; // gives 1st row of box need to change
		int bcol = (y / 3) * 3; // gives 1st column of box need to change
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(brow+i != x && bcol+j != y) {
					solve[brow+i][bcol+j].removeChoice(value);
				}
			}
		}
	}
	
	public boolean forwardCheck(Variable var, int val) { // true if no conflict
		int x = var.getX();
		int y = var.getY();
		for(int i = 0; i < 9; i++) {
			if(i != y && solve[x][i].getValue() == 0 && solve[x][i].containsChoice(val) && (solve[x][i].numChoices() == 1)) {
				return false;
			} else if(i != y && solve[x][i].getValue() == val) {
				return false;
			}
			if(i != x && solve[i][y].getValue() == 0 && solve[i][y].containsChoice(val) && (solve[i][y].numChoices() == 1)) {
				return false;
			} else if(i != x && solve[i][y].getValue() == val){
				return false;
			}
		}
		int brow = (x / 3) * 3; // gives 1st row of box need to change
		int bcol = (y / 3) * 3; // gives 1st column of box need to change
		for(int i = 0; i < 3; i++) {
			for(int j = 0; j < 3; j++) {
				if(brow+i != x && bcol+j != y && solve[brow+i][bcol+j].getValue() == 0 && solve[brow+i][bcol+j].containsChoice(val) && (solve[brow+i][bcol+j].numChoices() == 1)) {
					return false;
				} else if(brow+i != x && bcol+j != y && solve[brow+i][bcol+j].getValue() == val) {
					return false;
				}
			}
		}
		return true;
	}
	
	public boolean backtrack(Variable var, int varNum) { // recursive backtracking
		boolean works = false;
		for(int choice: var.getChoices()) {
			if(forwardCheck(var,choice)){
				var.setValue(choice);
				works = true;
				if(varNum + 1 < empty.size()) {
					boolean advance = backtrack(empty.get(varNum+1), varNum+1);
					if(!advance) {
						works = false;
						var.setValue(0);
					}
				}
			}
		}
		return works;
	}
	
	public void sortEmpty() {
		for(int i = 0; i < empty.size(); i++) {
			for(int j = i+1; j < empty.size(); j++) {
				if(empty.get(i).numChoices() > empty.get(j).numChoices()) {
					Variable temp = empty.get(i);
					empty.set(i, empty.get(j));
					empty.set(j, temp);
				}
			}
		}
	}
	
	public void printSolve() {
		for(int i = 0; i < 9; i++) {
			for(int j = 0; j < 9; j++) {
				System.out.print(solve[i][j].getValue() + " ");
			}
			System.out.println();
		}
	}
	
	
	public static void main(String[] args) throws IOException {
		Sudoku sudoku = new Sudoku();
	}
	
}
