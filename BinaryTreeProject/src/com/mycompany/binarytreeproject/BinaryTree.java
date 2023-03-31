package com.mycompany.binarytreeproject;

import java.util.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * This program takes an inorder equation, solves it, and converts it into a
 * binary tree. Then, it rewrites the equation in preorder and postorder. The
 * program creates a GUI for the user to input value to create the equation and
 * show the user the binary tree, preorder equation and postorder equation.
 * 
 * Inner Class:
 * 
 * GUI - implements ActionListener, contains all of the GUI-based items and methods
 * 
 * Methods:
 * 
 * BinaryTree() - constructs the BinaryTree
 * 
 * public static void main(String[] args) - the main method used to start up
 *                                          the program
 */
public class BinaryTree 
{
    private GUI gui;
    private Node head;
    
    private String equation;
    private String pastEquation;
    private int solution;
    private String postOrder;
    private String preOrder;
    
    /**
     * Constructs a BinaryTree.
     */
    public BinaryTree()
    {
        gui = new GUI();
        equation = "";
        pastEquation = "";
        postOrder = "";
        preOrder = "";
    }
    
    /**
     * A private class that creates the GUI for the user into interact with
     * the program.
     * 
     * Methods:
     * 
     * GUI - constructs the GUI
     * 
     * @param ActionEvent e
     * private actionPerformed(ActionEvent e) - processes each button press
     */
    private class GUI implements ActionListener
    {
        JFrame frame;
        
        JButton one;
        JButton two;
        JButton three;
        JButton four;
        JButton five;
        JButton six;
        JButton seven;
        JButton eight;
        JButton nine;
        JButton zero;
        
        JButton plus;
        JButton minus;
        JButton multiply;
        JButton divide;
        JButton power;
        
        JButton enter;
        JButton reset;
        
        JLabel equationLabel;
        JLabel pastEquationLabel;
        JLabel solutionLabel;
        JLabel postOrderLabel;
        JLabel preOrderLabel;
        JLabel fake;
        
        JPanel control;
        
        JLabel binaryTreeLabel;
        
        JPanel binaryTree;
        
        // Initiates the GUI
        private GUI()
        {
            frame = new JFrame();
            
            one = new JButton("1");
            one.addActionListener(this);
            two = new JButton("2");
            two.addActionListener(this);
            three = new JButton("3");
            three.addActionListener(this);
            four = new JButton("4");
            four.addActionListener(this);
            five = new JButton("5");
            five.addActionListener(this);
            six = new JButton("6");
            six.addActionListener(this);
            seven = new JButton("7");
            seven.addActionListener(this);
            eight = new JButton("8");
            eight.addActionListener(this);
            nine = new JButton("9");
            nine.addActionListener(this);
            zero = new JButton("0");
            zero.addActionListener(this);
            
            plus = new JButton("+");
            plus.addActionListener(this);
            minus = new JButton("-");
            minus.addActionListener(this);
            multiply = new JButton("*");
            multiply.addActionListener(this);
            divide = new JButton("/");
            divide.addActionListener(this);
            power = new JButton("^");
            power.addActionListener(this);
            
            enter = new JButton("Enter");
            enter.addActionListener(this);
            reset = new JButton("Reset");
            reset.addActionListener(this);
            
            equationLabel = new JLabel(equation);
            pastEquationLabel = new JLabel("Past Equation: " + pastEquation);
            solutionLabel = new JLabel("Solution: " + solution);
            postOrderLabel = new JLabel("PostOrder: " + postOrder);
            preOrderLabel = new JLabel("PreOrder: " + preOrder);
            fake = new JLabel();
            
            control = new JPanel();
            control.setBorder(BorderFactory.createEmptyBorder(100,100,10,80));
            control.setLayout(new GridLayout(4,5));
            
            control.add(equationLabel);
            control.add(pastEquationLabel);
            pastEquationLabel.setVisible(false);
            control.add(solutionLabel);
            solutionLabel.setVisible(false);
            control.add(postOrderLabel);
            postOrderLabel.setVisible(false);
            control.add(preOrderLabel);
            preOrderLabel.setVisible(false);
            control.add(fake);
            
            control.add(one);
            control.add(two);
            control.add(three);
            control.add(plus);
            plus.setVisible(false);
            control.add(multiply);
            multiply.setVisible(false);
            control.add(enter);
            
            enter.setVisible(false);
            control.add(four);
            control.add(five);
            control.add(six);
            control.add(minus);
            minus.setVisible(false);
            control.add(divide);
            divide.setVisible(false);
            control.add(reset);
            
            control.add(seven);
            control.add(eight);
            control.add(nine);
            control.add(zero);
            control.add(power);
            power.setVisible(false);
            
            binaryTreeLabel = new JLabel("lol");
            binaryTreeLabel.setHorizontalAlignment(JLabel.CENTER);
            binaryTreeLabel.setVerticalAlignment(JLabel.CENTER);
            
            binaryTree = new JPanel();
            binaryTree.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            binaryTree.setLayout(new GridLayout(1,1));
            
            binaryTree.add(binaryTreeLabel);
            
            frame.add(control, BorderLayout.PAGE_END);
            frame.add(binaryTree, BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }
        
        /**
         * Performs actions depending on what button is pressed.
         * 
         * @param e 
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource() == one)
            {
                equation += "1";
                this.setVisibility(true);
            }
            else if(e.getSource() == two)
            {
                equation += "2";
                this.setVisibility(true);
            }
            else if(e.getSource() == three)
            {
                equation += "3";
                this.setVisibility(true);
            }
            else if(e.getSource() == four)
            {
                equation += "4";
                this.setVisibility(true);
            }
            else if(e.getSource() == five)
            {
                equation += "5";
                this.setVisibility(true);
            }
            else if(e.getSource() == six)
            {
                equation += "6";
                this.setVisibility(true);
            }
            else if(e.getSource() == seven)
            {
                equation += "7";
                this.setVisibility(true);
            }
            else if(e.getSource() == eight)
            {
                equation += "8";
                this.setVisibility(true);
            }
            else if(e.getSource() == nine)
            {
                equation += "9";
                this.setVisibility(true);
            }
            else if(e.getSource() == zero)
            {
                equation += "0";
                this.setVisibility(true);
            }
            else if(e.getSource() == plus)
            {
                equation += "+";
                this.setVisibility(false);
            }
            else if(e.getSource() == minus)
            {
                equation += "-";
                this.setVisibility(false);
            }
            else if(e.getSource() == multiply)
            {
                equation += "*";
                this.setVisibility(false);
            }
            else if(e.getSource() == divide)
            {
                equation += "/";
                this.setVisibility(false);
                zero.setVisible(false);
            }
            else if(e.getSource() == power)
            {
                equation += "^";
                this.setVisibility(false);
            }
            else if(e.getSource() == enter)
            {
                convertEquation();
                pastEquation = equation;
                equation = "";
                solution = solve(head);
                postOrder = postOrder(head);
                preOrder = preOrder(head);
                pastEquationLabel.setText("Past Equation: " + pastEquation);
                pastEquationLabel.setVisible(true);
                solutionLabel.setText("Solution: " + solution);
                solutionLabel.setVisible(true);
                postOrderLabel.setText("PostOrder: " + postOrder);
                postOrderLabel.setVisible(true);
                preOrderLabel.setText("PreOrder: " + preOrder);
                preOrderLabel.setVisible(true);
                this.setVisibility(false);
                
                // this is so that the label can have multiple lines and whitespace
                String forLabel = "<html><pre>";
                String tree = BinaryTreePrinter(head);
                for(int i = 0; i < tree.length(); i++)
                {
                    if(tree.charAt(i) == '\n')
                    {
                        forLabel += "<br>";
                    }
                    else
                    {
                        forLabel += tree.charAt(i);
                    }
                }
                forLabel += "</html><pre>";
                binaryTreeLabel.setText(forLabel);
            }
            else{ // e.getSource() == reset
                equation = "";
                this.setVisibility(false);
            }
            equationLabel.setText(equation);
            
        }
        
        /**
         * A method to swap which buttons are available to the user.
         * 
         * @param numbersVisible 
         */
        private void setVisibility(boolean numbersVisible)
        {
            /*
            if a number is pressed, all number buttons turn off and all
            symbol buttons turn on
            */
            if(numbersVisible)
            {
                one.setVisible(false);
                two.setVisible(false);
                three.setVisible(false);
                four.setVisible(false);
                five.setVisible(false);
                six.setVisible(false);
                seven.setVisible(false);
                eight.setVisible(false);
                nine.setVisible(false);
                zero.setVisible(false);
                plus.setVisible(true);
                minus.setVisible(true);
                multiply.setVisible(true);
                divide.setVisible(true);
                power.setVisible(true);
                enter.setVisible(true);
            }
            /* 
            if a symbol is pressed, all symbol buttons turn off and all
            number buttons turn on
            */
            else
            {
                one.setVisible(true);
                two.setVisible(true);
                three.setVisible(true);
                four.setVisible(true);
                five.setVisible(true);
                six.setVisible(true);
                seven.setVisible(true);
                eight.setVisible(true);
                nine.setVisible(true);
                zero.setVisible(true);
                plus.setVisible(false);
                minus.setVisible(false);
                multiply.setVisible(false);
                divide.setVisible(false);
                power.setVisible(false);
                enter.setVisible(false);
            }
        }
        
    }
    
    /**
     * A private class that creates Nodes out of the data inputted by the
     * user through the GUI for the main class the use.
     * 
     * @param E
     * 
     * Methods:
     * 
     * @param char inputData
     * private Node(char inputData) - constructs a new Node
     * 
     * @param Node add
     * private void addLTree(Node add) - adds a child to the left of the Node
     * 
     * @param Node add
     * private void addRTree(Node add) - adds a child to the right of the Node
    */
    private class Node<E>
    {
        private char data;
        private Node<E> left;
        private Node<E> right;
        
        /**
         * Constructor for the Node.
         * 
         * @param inputData 
         */
        private Node(char inputData)
        {
            data = inputData;
        }
        
        private void addLTree(Node add)
        {
            left = add;
        }
        
        private void addRTree(Node add)
        {
            right = add;
        }
    }
    
    /**
     * A recursive method that converts the BinaryTree into a String to be
     * printed out. This version is used for the head (first root).
     * 
     * @param root
     * @return 
     */
    private String BinaryTreePrinter(Node root)
    {
        if(root == null)
        {
            return "";
        }
        
        String str = "";
        
        str += root.data;
        
        String pointerLeft = (root.right != null) ? "├── " : "└── ";
        
        str += BinaryTreePrinter("", pointerLeft , root.left, root.right != null);
        str += BinaryTreePrinter("", "└── " , root.right, false);
        
        return str;
    }
    
    /**
     * A recursive method that converts the BinaryTree into a String to be
     * printed out. This version is used for all non-first roots.
     * 
     * @param height
     * @param pointer
     * @param root
     * @return 
     */
    private String BinaryTreePrinter(String padding, String pointer, Node root, boolean hasRightSibling)
    {
        String str = "";
        if(root != null)
        {
            str += "\n";
            str += padding;
            str += pointer;
            str += root.data;
            
            if(hasRightSibling)
            {
                padding += "|   ";
            }
            else
            {
                padding += "    ";
            }
            
            String pointerLeft = (root.right != null) ? "├── " : "└── ";
            
            str += BinaryTreePrinter(padding, pointerLeft , root.left, root.right != null);
            str += BinaryTreePrinter(padding, "└── " , root.right, false);
        }
        return str;
    }
    
    /**
     * Converts the equation from the GUI input into a Binary Tree by using
     * for loops and an ArrayList in the findOperator() helper method.
    */
    private void convertEquation()
    {
        ArrayList<Node> list = new ArrayList<>();
        
        for(int i = 0; i < equation.length(); i++)
        {
            list.add(new Node(equation.charAt(i)));
        }
        
        list = findOperator('^', list);
        list = findOperator('*', list);
        list = findOperator('/', list);
        list = findOperator('+', list);
        list = findOperator('-', list);
        
        head = list.get(0);
    }
    
    /**
     * A helper method for convertEquation() that searches through an ArrayList<Node> for a given
     * operator in order to left the values to it's left and right into it's
     * children/subtrees.
     * 
     * @param operator
     * @param list
     * @return 
     */
    private ArrayList<Node> findOperator(char operator, ArrayList<Node> list)
    {
        boolean found = true;
        while(found)
        {
            found = false;
            for(int i = 0; i < list.size(); i++)
            {
                //System.out.println("Current i: " + i + " " + list.get(i).data);
                if(list.get(i).data == operator && list.get(i).left == null)
                {
                    //System.out.println("Operator: " + list.get(i).data);
                    list.get(i).left = list.get(i-1);
                    list.get(i).right = list.get(i+1);
                    list.remove(i+1);
                    list.remove(i-1);
                    i = list.size()+2;
                    found = true;
                }
            }
        }
        return list;
    }
    
    /**
     * Solves the equation.
     * 
     * @param equation
     * @return 
     */
    private int solve(Node root)
    {
        switch (root.data)
        {
            case '-':
                return solve(root.left) - solve(root.right);
            case '+':
                return solve(root.left) + solve(root.right);
            case '/':
                return solve(root.left) / solve(root.right);
            case '*':
                return solve(root.left) * solve(root.right);
            case '^':
                return (int)Math.pow(solve(root.left), solve(root.right));
            default:
                return Character.getNumericValue(root.data);
        }
    }
    
    /**
     * Converts the inorder equation into postorder.
     * 
     * @param root
     * @return 
     */
    private String postOrder(Node root)
    {
        String str = "";
        if(root != null)
        {
            str += postOrder(root.left);
            str += postOrder(root.right);
            str += root.data;
        }
        return str;
    }
    
    /**
     * Converts the inorder equation into preorder.
     * 
     * @param root
     * @return 
     */
    private String preOrder(Node root)
    {
        String str = "";
        if(root != null)
        {
            str += root.data;
            str += postOrder(root.left);
            str += postOrder(root.right);
        }
        return str;
    }
    
    public static void main(String[] args)
    {
        BinaryTree test = new BinaryTree();
    }
    
}
