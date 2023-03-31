// Nicholas Wilkinson Z00342652

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;

public class PageRemoverSim
{
    private GUI gui;
    private int numPages;
    private ArrayList<Integer> pageList;
    private Frame[] fifoList; // created in GUI when user inputs number of frames
    private int fifoInterrupts;
    private Frame[] lruList; // created in GUI when user inputs number of frames
    private int lruInterrupts;
    private ArrayList<Integer> enterOrder; // 1st element is first in
    private ArrayList<Integer> useOrder; // 1st element is least recently used
    
    private PageRemoverSim()
    {
        gui = new GUI();
        numPages = 0;
        pageList = new ArrayList<>();
        enterOrder = new ArrayList<>();
        useOrder = new ArrayList<>();
    }
    
    private class Frame
    {
        private final int number;
        private int pageNumber;
        
        private Frame(int number)
        {
            this.number = number;
            this.pageNumber = -1;
        }
    }
    
    private class GUI implements ActionListener
    {
        GridBagConstraints c;
        
        JFrame jframe;
        
        JPanel setup;
        
        JLabel frameNumLabel;
        ButtonGroup frameNum;
        JRadioButton twoFrames;
        JRadioButton threeFrames;
        JRadioButton fourFrames;
        JRadioButton fiveFrames;
        JLabel pageInputLabel;
        JFormattedTextField pageInput;
        JLabel pageInputList;
        JLabel nextStepLabel;
        JButton nextStepButton;
        JLabel outcome;
        
        JPanel tables;
        
        JLabel fifoLabel;
        JTable fifo;
        JLabel fifoFail;
        JLabel fifoSuccess;
        JLabel lruLabel;
        JTable lru;
        JLabel lruFail;
        JLabel lruSuccess;
        
        private GUI()
        {
            c = new GridBagConstraints();
            
            jframe = new JFrame();
            
            frameNumLabel = new JLabel("Select number of frames:");
            frameNum = new ButtonGroup();
            twoFrames = new JRadioButton("Two Frames");
            twoFrames.addActionListener(this);
            frameNum.add(twoFrames);
            threeFrames = new JRadioButton("Three Frames");
            threeFrames.addActionListener(this);
            frameNum.add(threeFrames);
            fourFrames = new JRadioButton("Four Frames");
            fourFrames.addActionListener(this);
            frameNum.add(fourFrames);
            fiveFrames = new JRadioButton("Five Frames");
            fiveFrames.addActionListener(this);
            frameNum.add(fiveFrames);
            pageInputLabel = new JLabel("Input page number to add to page allocation list (press enter):");
            pageInput = new JFormattedTextField(int.class);
            pageInput.addActionListener(this);
            pageInputList = new JLabel("List: ");
            nextStepLabel = new JLabel("Select 'Next' when you have filled in the above information:");
            nextStepButton = new JButton("Next");
            nextStepButton.addActionListener(this);
            outcome = new JLabel();
            
            setup = new JPanel();
            setup.setBorder(BorderFactory.createEmptyBorder());
            setup.setLayout(new GridBagLayout());
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            setup.add(frameNumLabel, c);
            c.gridx = 1;
            setup.add(twoFrames, c);
            c.gridx = 2;
            setup.add(threeFrames, c);
            c.gridx = 3;
            setup.add(fourFrames, c);
            c.gridx = 4;
            setup.add(fiveFrames, c);
            c.gridx = 0;
            c.gridy = 1;
            setup.add(pageInputLabel, c);
            c.gridx = 1;
            setup.add(pageInput, c);
            c.gridx = 0;
            c.gridy = 2;
            c.gridwidth = 5;
            setup.add(pageInputList, c);
            c.gridx = 0;
            c.gridy = 3;
            c.gridwidth = 1;
            setup.add(nextStepLabel, c);
            c.gridx = 1;
            setup.add(nextStepButton, c);
            c.gridx = 0;
            c.gridy = 4;
            c.gridwidth = 5;
            setup.add(outcome, c);
            
            jframe.add(setup, BorderLayout.PAGE_START);
            jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            jframe.pack();
            jframe.setVisible(true);
            
            tables = new JPanel();
            tables.setBorder(BorderFactory.createEmptyBorder());
            tables.setLayout(new GridBagLayout());
            
            jframe.add(tables, BorderLayout.CENTER);
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource() == twoFrames)
            {
                numPages = 2;
            }
            else if(e.getSource() == threeFrames)
            {
                numPages = 3;
            }
            else if(e.getSource() == fourFrames)
            {
                numPages = 4;
            }
            else if(e.getSource() == fiveFrames)
            {
                numPages = 5;
            }
            else if(e.getSource() == pageInput)
            {
                if(!pageInput.getText().equals(null))
                {
                    try
                    {
                        int input = Integer.parseInt(pageInput.getText());
                        pageList.add(input);
                        String list = "List: ";
                        for(int i = 0; i < pageList.size(); i++)
                        {
                            list += "" + pageList.get(i);
                            if(i != pageList.size()-1)
                            {
                                list += ", ";
                            }
                        }
                        pageInputList.setText(list);
                        jframe.validate();
                        jframe.repaint();
                    }
                    catch(Exception a)
                    {}
                }
            }
            else if(e.getSource() == nextStepButton)
            {
                if(numPages < 2 && pageList.isEmpty())
                {
                    outcome.setText("Number of frames and list of page allocations not inputted!");
                }
                else if(numPages < 2)
                {
                    outcome.setText("Number of frames not inputted!");
                }
                else if(pageList.isEmpty())
                {
                    outcome.setText("List of page allocations not inputted!");
                }
                else
                {
                    outcome.setText("Success!");
                    fifoList = new Frame[numPages];
                    lruList = new Frame[numPages];
                    for(int i = 0; i < fifoList.length; i++)
                    {
                        fifoList[i] = new Frame(i+1);
                        lruList[i] = new Frame(i+1);
                    }
                    fifoInterrupts = 0;
                    lruInterrupts = 0;
                    fifoLabel = new JLabel("First in First Out (FIFO) Algorithm:");
                    fifo = fifo();
                    lruLabel = new JLabel("Least Recently Used (LRU) Algorithm:");
                    lru = lru();
                    fifoFail = new JLabel("Fail Rate: " + fifoInterrupts + "/" + pageList.size()+ "    ");
                    fifoSuccess = new JLabel("Success Rate: " + (pageList.size()-fifoInterrupts) + "/" + pageList.size());
                    lruFail = new JLabel("Fail Rate: " + lruInterrupts + "/" + pageList.size() + "    ");
                    lruSuccess = new JLabel("Success Rate: " + (pageList.size()-lruInterrupts) + "/" + pageList.size());
                    pageList.clear();
                    pageInputList.setText("List: ");
                    
                    jframe.remove(tables);
                    jframe.validate();
                    jframe.repaint();
                    
                    tables = new JPanel();
                    tables.setBorder(BorderFactory.createEmptyBorder());
                    tables.setLayout(new GridBagLayout());
                    c.fill = GridBagConstraints.HORIZONTAL;
                    c.gridx = 0;
                    c.gridy = 0;
                    tables.add(fifoLabel, c);
                    c.gridx = 0;
                    c.gridy = 1;
                    c.gridwidth = 3;
                    tables.add(fifo, c);
                    c.gridy = 2;
                    c.gridwidth = 1;
                    tables.add(fifoFail, c);
                    c.gridx = 2;
                    tables.add(fifoSuccess, c);
                    c.gridx = 0;
                    c.gridy = 3;
                    tables.add(lruLabel, c);
                    c.gridx = 0;
                    c.gridy = 4;
                    c.gridwidth = 3;
                    tables.add(lru, c);
                    c.gridy = 5;
                    c.gridwidth = 1;
                    tables.add(lruFail, c);
                    c.gridx = 2;
                    tables.add(lruSuccess, c);
                    
                    jframe.add(tables, BorderLayout.CENTER);
                }
                jframe.validate();
                jframe.repaint();
            }
        }
    }
    
    // allocates and deallocates page with FIFO method
    private JTable fifo()
    {
        JTable table = new JTable(fifoList.length+2, pageList.size()+1);
        table.setValueAt("Page Allocated:", 0, 0);
        for(int i = 0; i < fifoList.length; i++)
        {
            table.setValueAt("Frame " + (i+1), i+1, 0);
        }
        table.setValueAt("Interrupt:", fifoList.length+1, 0);
        for(int i = 0; i < pageList.size(); i++)
        {
            table.setValueAt(pageList.get(i), 0, i+1);
            boolean interrupt = false;
            boolean duplicate = false;
            for(Frame e : fifoList)
            {
                if (e.pageNumber == pageList.get(i))
                {
                    duplicate = true;
                }
            }
            if(!duplicate)
            {
                interrupt = true;
                fifoInterrupts++;
                boolean placed = false;
                for(int j = 0; j < fifoList.length; j++)
                {
                    if(fifoList[j].pageNumber < 0) // checks for empty frame to allocate into
                    {
                        fifoList[j].pageNumber = pageList.get(i);
                        placed = true;
                        j += fifoList.length;
                        enterOrder.add(pageList.get(i));
                    }
                }
                if(!placed)
                {
                    for(int j = 0; j < fifoList.length; j++) // allocates page
                    {
                        if(fifoList[j].pageNumber == enterOrder.get(0))
                        {
                            fifoList[j].pageNumber = pageList.get(i);
                            enterOrder.remove(0);
                            enterOrder.add(pageList.get(i));
                            j += fifoList.length;
                        }
                    }
                }
            }
            for(int j = 0; j < fifoList.length; j++)
            {
                if(fifoList[j].pageNumber > -1)
                {
                    table.setValueAt(fifoList[j].pageNumber, j+1, i+1);
                }
            }
            if(interrupt)
            {
                table.setValueAt("*", fifoList.length+1, i+1);
            }
        }
        return table;
    }
    
    // allocates and deallocates page with LRU method
    private JTable lru()
    {
        JTable table = new JTable(lruList.length+2, pageList.size()+1);
        table.setValueAt("Page Allocated:", 0, 0);
        for(int i = 0; i < lruList.length; i++)
        {
            table.setValueAt("Frame " + (i+1), i+1, 0);
        }
        table.setValueAt("Interrupt:", lruList.length+1, 0);
        for(int i = 0; i < pageList.size(); i++)
        {
            table.setValueAt(pageList.get(i), 0, i+1);
            boolean interrupt = false;
            boolean duplicate = false;
            for(Frame e : lruList)
            {
                if (e.pageNumber == pageList.get(i))
                {
                    duplicate = true;
                    for(int j = 0; j < useOrder.size(); j++)
                    {
                        if(useOrder.get(j).equals(pageList.get(i)))
                        {
                            useOrder.remove(j);
                            useOrder.add(pageList.get(i));
                            j += useOrder.size();
                        }
                    }
                }
            }
            if(!duplicate)
            {
                interrupt = true;
                lruInterrupts++;
                boolean placed = false;
                for(int j = 0; j < lruList.length; j++)
                {
                    if(lruList[j].pageNumber < 0)
                    {
                        lruList[j].pageNumber = pageList.get(i);
                        placed = true;
                        useOrder.add(pageList.get(i));
                        j += lruList.length;
                    }
                }
                if(!placed)
                {
                    for(int j = 0; j < lruList.length; j++)
                    {
                        if(lruList[j].pageNumber == useOrder.get(0))
                        {
                            lruList[j].pageNumber = pageList.get(i);
                            System.out.println(useOrder.remove(0));
                            useOrder.add(pageList.get(i));
                            System.out.println(useOrder.get(0));
                            j += lruList.length;
                        }
                    }
                }
            }
            for(int j = 0; j < lruList.length; j++)
            {
                if(lruList[j].pageNumber > -1)
                {
                    table.setValueAt(lruList[j].pageNumber, j+1, i+1);
                }
            }
            if(interrupt)
            {
                table.setValueAt("*", lruList.length+1, i+1);
            }
        }
        return table;
    }
    
    public static void main(String[] args)
    {
        PageRemoverSim main = new PageRemoverSim();
    }
}
