// Nicholas Wilkinson Z00342652

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;

/**
 *
 * @author ichol
 */
public class VirtualJobSimulator
{
    private final GUI gui;
    private int nextJobNum;
    private ArrayList<Job> rawList;
    private FCFS fcfs;
    private SJN sjn;
    private SRT srt;
    private RR rr;
    private int quantumForRR;
    private int timeStamp;
    
    public VirtualJobSimulator()
    {
        gui = new GUI();
        nextJobNum = 0;
        rawList = new ArrayList<>();
        timeStamp = 0;
        quantumForRR = 0;
    }
    
    private class Job
    {
        private final int num;
        private final int cycle;
        private int remaining;
        private final int arrival;
        private int departure;
        
        private Job(int nu, int siz, int arr)
        {
            num = nu;
            cycle = siz;
            remaining = cycle;
            arrival = arr;
        }
        
        private Job(int siz, int arr)
        {
            this(nextJobNum, siz, arr);
            nextJobNum++;
        }
        
        @Override
        public String toString()
        {
            return ("Job " + num + ", Cy " + cycle + ", Ar " + arrival);
        }
    }
    
    private class FCFS // first-come, first-serve
    {
        private final ArrayList<Job> list;
        private final ArrayList<Integer> processOrder;
        private final ArrayList<Integer> processOrderTime;
        private final ArrayList<Job> processed;
        private Job processing;
        
        private FCFS()
        {
            list = new ArrayList<>();
            processOrder = new ArrayList<>();
            processOrderTime = new ArrayList<>();
            processed = new ArrayList<>();
        }
        
        private void add(Job job)
        {
            list.add(new Job(job.num, job.cycle, job.arrival));
        }
        
        private void next()
        {
            processOrder.add(processing.num);
            processOrderTime.add(timeStamp);
            processing.departure = timeStamp;
            processed.add(processing);
            if(!list.isEmpty())
            {
                processing = list.get(0);
                list.remove(0);
            }
            else
            {
                processing = null;
            }
        }
        
        private void cycle()
        {
            if(processing == null && !list.isEmpty())
            {
                processing = list.get(0);
                list.remove(0);
            }
            if(processing != null)
            {
                processing.remaining--;
                if(processing.remaining <= 0)
                {
                    next();
                }
            }
        }
        
        private String turnaroundAll()
        {
            String output = "";
            for(Job i: processed)
            {
                output += (i.departure - i.arrival) + " | ";
            }
            return output;
        }
        
        private String waitingAll()
        {
            String output = "";
            for(Job i: processed)
            {
                output += ((i.departure - i.arrival) - i.cycle) + " | ";
            }
            return output;
        }
        
        private int getTurnaroundTop()
        {
            int output = 0;
            for(Job i: processed)
            {
                output += i.departure - i.arrival;
            }
            return output;
        }
        
        private int getWaitingTop()
        {
            int output = 0;
            for(Job i: processed)
            {
                output += i.departure - i.arrival - i.cycle;
            }
            return output;
        }
    }
    
    private class SJN // shortest job next
    {
        private final ArrayList<Job> list;
        private final ArrayList<Integer> processOrder;
        private final ArrayList<Integer> processOrderTime;
        private final ArrayList<Job> processed;
        private Job processing;
        
        private SJN()
        {
            list = new ArrayList<>();
            processOrder = new ArrayList<>();
            processOrderTime = new ArrayList<>();
            processed = new ArrayList<>();
        }
        
        private void add(Job job)
        {
            if(list.isEmpty())
            {
                list.add(new Job(job.num, job.cycle, job.arrival));
            }
            else
            {
                boolean added = false;
                for(int i = 0; i < list.size(); i++)
                {
                    if(job.cycle < list.get(i).remaining)
                    {
                        list.add(i, new Job(job.num, job.cycle, job.arrival));
                        i += list.size();
                        added = true;
                    }
                }
                if(!added)
                {
                    list.add(new Job(job.num, job.cycle, job.arrival));
                }
            }
        }
        
        private void next()
        {
            processOrder.add(processing.num);
            processOrderTime.add(timeStamp);
            processing.departure = timeStamp;
            processed.add(processing);
            if(!list.isEmpty())
            {
                processing = list.get(0);
                list.remove(0);
            }
            else
            {
                processing = null;
            }
        }
        
        private void cycle()
        {
            if(processing == null && !list.isEmpty())
            {
                processing = list.get(0);
                list.remove(0);
            }
            if(processing != null)
            {
                processing.remaining--;
                if(processing.remaining <= 0)
                {
                    next();
                }
            }
        }
        
        private String turnaroundAll()
        {
            String output = "";
            for(Job i: processed)
            {
                output += (i.departure - i.arrival) + " | ";
            }
            return output;
        }
        
        private String waitingAll()
        {
            String output = "";
            for(Job i: processed)
            {
                output += ((i.departure - i.arrival) - i.cycle) + " | ";
            }
            return output;
        }
        
        private int getTurnaroundTop()
        {
            int output = 0;
            for(Job i: processed)
            {
                output += i.departure - i.arrival;
            }
            return output;
        }
        
        private int getWaitingTop()
        {
            int output = 0;
            for(Job i: processed)
            {
                output += i.departure - i.arrival - i.cycle;
            }
            return output;
        }
    }
    
    private class SRT // shortest remaining time
    {
        private final ArrayList<Job> list;
        private final ArrayList<Integer> processOrder;
        private final ArrayList<Integer> processOrderTime;
        private final ArrayList<Job> processed;
        private Job processing;
        
        private SRT()
        {
            list = new ArrayList<>();
            processOrder = new ArrayList<>();
            processOrderTime = new ArrayList<>();
            processed = new ArrayList<>();
        }
        
        private void add(Job job)
        {
            if(processing != null && processing.remaining > job.remaining)
            {
                processOrder.add(processing.num);
                processOrderTime.add(timeStamp);
                Job removed = processing;
                processing = new Job(job.num, job.cycle, job.arrival);
                list.add(0, removed);
            }
            else if(list.isEmpty())
            {
                list.add(new Job(job.num, job.cycle, job.arrival));
            }
            else
            {
                boolean added = false;
                for(int i = 0; i < list.size(); i++)
                {
                    if(job.cycle < list.get(i).remaining)
                    {
                        list.add(i, new Job(job.num, job.cycle, job.arrival));
                        i += list.size();
                        added = true;
                    }
                }
                if(!added)
                {
                    list.add(new Job(job.num, job.cycle, job.arrival));
                }
            }
        }
        
        private void next()
        {
            processOrder.add(processing.num);
            processOrderTime.add(timeStamp);
            processing.departure = timeStamp;
            processed.add(processing);
            if(!list.isEmpty())
            {
                processing = list.get(0);
                list.remove(0);
            }
            else
            {
                processing = null;
            }
        }
        
        private void cycle()
        {
            if(processing == null && !list.isEmpty())
            {
                processing = list.get(0);
                list.remove(0);
            }
            if(processing != null)
            {
                processing.remaining--;
                if(processing.remaining <= 0)
                {
                    next();
                }
            }
        }
        
        private String turnaroundAll()
        {
            String output = "";
            for(Job i: processed)
            {
                output += (i.departure - i.arrival) + " | ";
            }
            return output;
        }
        
        private String waitingAll()
        {
            String output = "";
            for(Job i: processed)
            {
                output += ((i.departure - i.arrival) - i.cycle) + " | ";
            }
            return output;
        }
        
        private int getTurnaroundTop()
        {
            int output = 0;
            for(Job i: processed)
            {
                output += i.departure - i.arrival;
            }
            return output;
        }
        
        private int getWaitingTop()
        {
            int output = 0;
            for(Job i: processed)
            {
                output += i.departure - i.arrival - i.cycle;
            }
            return output;
        }
    }
    
    private class RR // round robin
    {
        private final ArrayList<Job> list;
        private final ArrayList<Integer> processOrder;
        private final ArrayList<Integer> processOrderTime;
        private final ArrayList<Job> processed;
        private Job processing;
        private final int quantum;
        private int quantumLeft;
        
        private RR(int qua)
        {
            list = new ArrayList<>();
            processOrder = new ArrayList<>();
            processOrderTime = new ArrayList<>();
            processed = new ArrayList<>();
            quantum = qua;
            quantumLeft = quantum;
        }
        
        private void add(Job job)
        {
            list.add(new Job(job.num, job.cycle, job.arrival));
        }
        
        private void next()
        {
            processOrder.add(processing.num);
            processOrderTime.add(timeStamp);
            if(!list.isEmpty())
            {
                
                if(processing.remaining > 0)
                {
                    list.add(processing);
                }
                else
                {
                    processing.departure = timeStamp;
                    processed.add(processing);
                }
                processing = list.get(0);
                list.remove(0);
            }
            else if(processing.remaining <= 0)
            {
                processing.departure = timeStamp;
                processed.add(processing);
                processing = null;
            }
            // else processing > 0, list is empty and process not done yet so keep going
        }
        
        private void cycle()
        {
            if(processing == null && !list.isEmpty())
            {
                processing = list.get(0);
                list.remove(0);
            }
            if(processing != null)
            {
                processing.remaining--;
                quantumLeft--;
                if(processing.remaining <= 0 || quantumLeft <= 0)
                {
                    quantumLeft = quantum;
                    next();
                }
            }
        }
        
        private String turnaroundAll()
        {
            String output = "";
            for(Job i: processed)
            {
                output += (i.departure - i.arrival) + " | ";
            }
            return output;
        }
        
        private String waitingAll()
        {
            String output = "";
            for(Job i: processed)
            {
                output += ((i.departure - i.arrival) - i.cycle) + " | ";
            }
            return output;
        }
        
        private int getTurnaroundTop()
        {
            int output = 0;
            for(Job i: processed)
            {
                output += i.departure - i.arrival;
            }
            return output;
        }
        
        private int getWaitingTop()
        {
            int output = 0;
            for(Job i: processed)
            {
                output += i.departure - i.arrival - i.cycle;
            }
            return output;
        }
    }
    
    private class GUI implements ActionListener
    {
        GridBagConstraints c;
        
        JFrame frame;
        
        JPanel input;
        
        JLabel cycleLabel;
        JFormattedTextField cycle;
        JLabel arrivalLabel;
        JFormattedTextField arrival;
        JButton add;
        JLabel addOutcome;
        JLabel listLabel;
        JLabel quantumLabel;
        JFormattedTextField quantum;
        JLabel currentQuantum;
        JLabel quantumOutcome;
        JButton process;
        JLabel processOutcome;
        
        JPanel output;
        
        JLabel outputList;
        JLabel fcfsLabel;
        JTable fcfsTable;
        JLabel fcfsTurnaroundAll;
        JLabel fcfsWaitingAll;
        JLabel fcfsTurnaround;
        JLabel fcfsWaiting;
        JLabel sjnLabel;
        JTable sjnTable;
        JLabel sjnTurnaroundAll;
        JLabel sjnWaitingAll;
        JLabel sjnTurnaround;
        JLabel sjnWaiting;
        JLabel srtLabel;
        JTable srtTable;
        JLabel srtTurnaroundAll;
        JLabel srtWaitingAll;
        JLabel srtTurnaround;
        JLabel srtWaiting;
        JLabel rrLabel;
        JTable rrTable;
        JLabel rrTurnaroundAll;
        JLabel rrWaitingAll;
        JLabel rrTurnaround;
        JLabel rrWaiting;
        
        private GUI()
        {
            c = new GridBagConstraints();
            
            frame = new JFrame();
            
            input = new JPanel();
            
            cycleLabel = new JLabel("Enter cycle time of new job:");
            cycle = new JFormattedTextField(int.class);
            cycle.setValue("");
            cycle.setText("");
            arrivalLabel = new JLabel("Enter arrival time of the new job (defaults to 0):");
            arrival = new JFormattedTextField(int.class);
            arrival.setValue("");
            arrival.setText("");
            add = new JButton("Press to add new job");
            add.addActionListener(this);
            addOutcome = new JLabel();
            listLabel = new JLabel("Job List:");
            quantumLabel = new JLabel("Enter quantum time (press enter when inputted):");
            quantum = new JFormattedTextField(int.class);
            quantum.addActionListener(this);
            quantum.setValue("");
            quantum.setText("");
            currentQuantum = new JLabel("Current Quantum Time: " + quantumForRR);
            quantumOutcome = new JLabel();
            process = new JButton("Press when all fields are filled");
            process.addActionListener(this);
            processOutcome = new JLabel();
            
            input = new JPanel();
            input.setBorder(BorderFactory.createEmptyBorder());
            input.setLayout(new GridBagLayout());
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            c.gridwidth = 1;
            c.weightx = 0.5;
            input.add(cycleLabel, c);
            c.gridx++;
            input.add(cycle, c);
            c.gridx++;
            input.add(arrivalLabel, c);
            c.gridx++;
            input.add(arrival, c);
            c.gridx++;
            input.add(add, c);
            c.gridx = 0;
            c.gridy++;
            input.add(addOutcome, c);
            c.gridy++;
            c.gridwidth = 5;
            input.add(listLabel, c);
            c.gridy++;
            c.gridwidth = 1;
            input.add(quantumLabel, c);
            c.gridx++;
            input.add(quantum, c);
            c.gridx++;
            input.add(currentQuantum, c);
            c.gridx = 0;
            c.gridy++;
            input.add(quantumOutcome, c);
            c.gridy++;
            input.add(process, c);
            c.gridy++;
            input.add(processOutcome, c);
            
            frame.add(input, BorderLayout.PAGE_START);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource() == add)
            {
                if(!cycle.getText().equals("") && !arrival.getText().equals("")){
                    try
                    {
                        if(Integer.parseInt(cycle.getText()) <= 0 && Integer.parseInt(arrival.getText()) < 0)
                        {
                            cycle.setValue("");
                            arrival.setValue("");
                            cycle.setText("");
                            arrival.setText("");
                            addOutcome.setText("FAILURE: cycle and arrival are too small");
                        }
                        else if(Integer.parseInt(cycle.getText()) <= 0)
                        {
                            cycle.setValue("");
                            arrival.setValue("");
                            cycle.setText("");
                            arrival.setText("");
                            addOutcome.setText("FAILURE: cycle is too small");
                        }
                        else if(Integer.parseInt(arrival.getText()) < 0)
                        {
                            cycle.setValue("");
                            arrival.setValue("");
                            cycle.setText("");
                            arrival.setText("");
                            addOutcome.setText("FAILURE: arrival is too small");
                        }
                        else{
                            rawList.add(new Job(Integer.parseInt(cycle.getText()), Integer.parseInt(arrival.getText())));
                            cycle.setValue("");
                            arrival.setValue("");
                            cycle.setText("");
                            arrival.setText("");
                            addOutcome.setText("");
                            updateListLabel();
                        }
                    }
                    catch(NumberFormatException f)
                    {}
                }
                else if(!cycle.getText().equals(""))
                {
                    try
                    {
                        if(Integer.parseInt(cycle.getText()) <= 0)
                        {
                            cycle.setValue("");
                            arrival.setValue("");
                            cycle.setText("");
                            arrival.setText("");
                            addOutcome.setText("FAILURE: cycle is too small");
                        }
                        else
                        {
                            rawList.add(new Job(Integer.parseInt(cycle.getText()), 0));
                            cycle.setValue("");
                            cycle.setText("");
                            addOutcome.setText("");
                            updateListLabel();
                        }
                    }
                    catch(NumberFormatException f)
                    {}
                }
                else
                {
                    arrival.setValue("");
                    arrival.setText("");
                    addOutcome.setText("FAILURE: no cycle size inputted");
                }
            }
            else if(e.getSource() == quantum)
            {
                if(!quantum.getText().equals(""))
                {
                    try
                    {
                        if(Integer.parseInt(quantum.getText()) <= 0)
                        {
                            quantum.setValue("");
                            quantum.setText("");
                            quantumOutcome.setText("FAILURE: qunatum is too small");
                        }
                        else
                        {
                            quantumForRR = Integer.parseInt(quantum.getText());
                            currentQuantum.setText(quantum.getText());
                            quantum.setValue("");
                            quantum.setText("");
                            quantumOutcome.setText("");
                        }
                    }
                    catch(NumberFormatException f)
                    {}
                }
            }
            else if(e.getSource() == process)
            {
                if(rawList.isEmpty() && quantumForRR <= 0)
                {
                    processOutcome.setText("FAILURE: no jobs inputted and no quantum size");
                }
                else if(rawList.isEmpty())
                {
                    processOutcome.setText("FAILURE: no jobs inputted");
                }
                else if(quantumForRR <= 0)
                {
                    processOutcome.setText("FAILURE: no quantum size");
                }
                else
                {
                    output();
                    processOutcome.setText("");
                    listLabel.setText("Job List:");
                    currentQuantum.setText("0");
                    rawList = new ArrayList<>();
                    quantumForRR = 0;
                    timeStamp = 0;
                    nextJobNum = 0;
                }
            }
        }
        
        private void updateListLabel()
        {
            String str = "";
            for (int i = 0; i < rawList.size(); i++)
            {
                str += rawList.get(i).toString();
                if(i < rawList.size()-1)
                {
                    str += " | ";
                }
            }
            listLabel.setText("Job List: " + str);
        }
        
        private void output()
        {
            if(output != null)
            {
                frame.remove(output);
            }
            fcfs = new FCFS();
            sjn = new SJN();
            srt = new SRT();
            rr = new RR(quantumForRR);
            cycle();
            while(!rawList.isEmpty() || fcfs.processing != null || sjn.processing != null || srt.processing != null || rr.processing != null)
            {
                cycle();
            }
            
            output = new JPanel();
            
            outputList = new JLabel(listLabel.getText());
            fcfsLabel = new JLabel("First-Come, First-Serve:");
            fcfsTable = new JTable(2, fcfs.processOrder.size());
            for(int i = 0; i < fcfs.processOrder.size(); i++)
            {
                fcfsTable.setValueAt("Job " + fcfs.processOrder.get(i), 0, i);
                fcfsTable.setValueAt(fcfs.processOrderTime.get(i), 1, i);
            }
            fcfsTurnaroundAll = new JLabel("Turnaround of each job: " + fcfs.turnaroundAll());
            fcfsWaitingAll = new JLabel("Waiting time of each job: " + fcfs.waitingAll());
            fcfsTurnaround = new JLabel("Average Turnaround: " + fcfs.getTurnaroundTop() + "/" + fcfs.processed.size());
            fcfsWaiting = new JLabel("Average Waiting: " + fcfs.getWaitingTop() + "/" + fcfs.processed.size());
            sjnLabel = new JLabel("Shortest Job Next");
            sjnTable = new JTable(2, sjn.processOrder.size());
            for(int i = 0; i < sjn.processOrder.size(); i++)
            {
                sjnTable.setValueAt("Job " + sjn.processOrder.get(i), 0, i);
                sjnTable.setValueAt(sjn.processOrderTime.get(i), 1, i);
            }
            sjnTurnaroundAll = new JLabel("Turnaround of each job: " + sjn.turnaroundAll());
            sjnWaitingAll = new JLabel("Waiting time of each job: " + sjn.waitingAll());
            sjnTurnaround = new JLabel("Average Turnaround: " + sjn.getTurnaroundTop() + "/" + sjn.processed.size());
            sjnWaiting = new JLabel("Average Waiting: " + sjn.getWaitingTop() + "/" + sjn.processed.size());
            srtLabel = new JLabel("Shortest Remaining Time:");
            srtTable = new JTable(2, srt.processOrder.size());
            for(int i = 0; i < srt.processOrder.size(); i++)
            {
                srtTable.setValueAt("Job " + srt.processOrder.get(i), 0, i);
                srtTable.setValueAt(srt.processOrderTime.get(i), 1, i);
            }
            srtTurnaroundAll = new JLabel("Turnaround of each job: " + srt.turnaroundAll());
            srtWaitingAll = new JLabel("Waiting time of each job: " + srt.waitingAll());
            srtTurnaround = new JLabel("Average Turnaround: " + srt.getTurnaroundTop() + "/" + srt.processed.size());
            srtWaiting = new JLabel("Average Waiting: " + srt.getWaitingTop() + "/" + srt.processed.size());
            rrLabel = new JLabel("Round Robin:");
            rrTable = new JTable(2, rr.processOrder.size());
            for(int i = 0; i < rr.processOrder.size(); i++)
            {
                rrTable.setValueAt("Job " + rr.processOrder.get(i), 0, i);
                rrTable.setValueAt(rr.processOrderTime.get(i), 1, i);
            }
            rrTurnaroundAll = new JLabel("Turnaround of each job: " + rr.turnaroundAll());
            rrWaitingAll = new JLabel("Waiting time of each job: " + rr.waitingAll());
            rrTurnaround = new JLabel("Average Turnaround: " + rr.getTurnaroundTop() + "/" + rr.processed.size());
            rrWaiting = new JLabel("Average Waiting: " + rr.getWaitingTop() + "/" + rr.processed.size());
            
            output = new JPanel();
            output.setBorder(BorderFactory.createEmptyBorder());
            output.setLayout(new GridBagLayout());
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            output.add(outputList, c);
            c.gridy++;
            output.add(fcfsLabel, c);
            c.gridy++;
            output.add(fcfsTable, c);
            c.gridy++;
            output.add(fcfsTurnaroundAll, c);
            c.gridy++;
            output.add(fcfsWaitingAll, c);
            c.gridy++;
            output.add(fcfsTurnaround, c);
            c.gridy++;
            output.add(fcfsWaiting, c);
            c.gridy++;
            output.add(sjnLabel, c);
            c.gridy++;
            output.add(sjnTable, c);
            c.gridy++;
            output.add(sjnTurnaroundAll, c);
            c.gridy++;
            output.add(sjnWaitingAll, c);
            c.gridy++;
            output.add(sjnTurnaround, c);
            c.gridy++;
            output.add(sjnWaiting, c);
            c.gridy++;
            output.add(srtLabel, c);
            c.gridy++;
            output.add(srtTable, c);
            c.gridy++;
            output.add(srtTurnaroundAll, c);
            c.gridy++;
            output.add(srtWaitingAll, c);
            c.gridy++;
            output.add(srtTurnaround, c);
            c.gridy++;
            output.add(srtWaiting, c);
            c.gridy++;
            output.add(rrLabel, c);
            c.gridy++;
            output.add(rrTable, c);
            c.gridy++;
            output.add(rrTurnaroundAll, c);
            c.gridy++;
            output.add(rrWaitingAll, c);
            c.gridy++;
            output.add(rrTurnaround, c);
            c.gridy++;
            output.add(rrWaiting, c);
            
            frame.add(output, BorderLayout.CENTER);
            frame.validate();
            frame.repaint();
        }
    }
    
    private void cycle()
    {
        if(timeStamp == 0)
        {
            for(int i = 0; i < rawList.size(); i++)
            {
                if(rawList.get(i).arrival == timeStamp)
                {
                    fcfs.add(rawList.get(i));
                    sjn.add(rawList.get(i));
                    srt.add(rawList.get(i));
                    rr.add(rawList.get(i));
                    rawList.remove(i);
                    i--;
                }
            }
        }
        timeStamp++;
        fcfs.cycle();
        sjn.cycle();
        srt.cycle();
        rr.cycle();
        for(int i = 0; i < rawList.size(); i++)
        {
            if(rawList.get(i).arrival == timeStamp)
            {
                fcfs.add(rawList.get(i));
                sjn.add(rawList.get(i));
                srt.add(rawList.get(i));
                rr.add(rawList.get(i));
                rawList.remove(i);
                i--;
            }
        }
    }
    
    public static void main(String[] args)
    {
        VirtualJobSimulator sim = new VirtualJobSimulator();
    }
}
