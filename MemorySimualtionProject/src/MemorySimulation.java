// Nicholas Wilkinson

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

// program
public class MemorySimulation
{
    private FixedList fixedList;
    private DynamicList dynamicList;
    // how much memory there is in total
    private int memSize;
    // 0 = first-fit, 1 = best-fit, 2 = next-fit, 3 = worst-fit
    private int allocationMode;
    private GUI gui;
    
    public MemorySimulation()
    {
        fixedList = new FixedList();
        dynamicList = new DynamicList();
        memSize = 100;
        allocationMode = 0;
        gui = new GUI();
    }
    
    // class that controls the fixed partition list
    private class FixedList
    {
        private ArrayList<Partition> partitions;
        // queue for waiting jobs
        private ArrayList<Job> queue;
        private int memRemaining;
        private int previous;
        private int nextJobNumber; // this is used to name each of the jobs, and will increment as jobs are added
        private int frag; // fragmentation
        
        private FixedList()
        {
            partitions = new ArrayList<>();
            queue = new ArrayList<>();
            memRemaining = 100;
            previous = 0;
            nextJobNumber = 1;
            frag = 0;
        }
        
        private void addJob(int size)
        {
            switch (allocationMode)
            {
                case 0: // first-fit
                {
                    Job adding = new Job(size, nextJobNumber);
                    nextJobNumber++;
                    boolean placed = false;
                    for(int i = 0; i < partitions.size(); i++)
                    {
                        if(partitions.get(i).size >= adding.size && partitions.get(i).empty)
                        {
                            partitions.get(i).fill(adding);
                            placed = true;
                            frag += partitions.get(i).size - adding.size;
                            i += partitions.size();
                        }
                    }
                    if(!placed)
                    {
                        queue.add(adding);
                    }
                    break;
                }
                case 1: // best-fit
                {
                    Job adding = new Job(size, nextJobNumber);
                    nextJobNumber++;
                    int best = -2;
                    for(int i = 0; i < partitions.size(); i++)
                    {
                        if(partitions.get(i).size >= adding.size && partitions.get(i).empty)
                        {
                            if(best < 0)
                            {
                                best = i;
                            }
                            else if(partitions.get(i).size < partitions.get(best).size)
                            {
                                best = i;
                            }
                        }
                    }
                    if(best < 0)
                    {
                        queue.add(adding);
                    }
                    else
                    {
                        partitions.get(best).fill(adding);
                        frag += partitions.get(best).size - adding.size;
                    }
                    break;
                }
                case 2: // next-fit
                {
                    Job adding = new Job(size, nextJobNumber);
                    nextJobNumber++;
                    boolean placed = false;
                    for(int i = previous; i < partitions.size(); i++)
                    {
                        if(partitions.get(i).size >= adding.size && partitions.get(i).empty)
                        {
                            partitions.get(i).fill(adding);
                            placed = true;
                            frag += partitions.get(i).size - adding.size;
                            previous = i;
                            i += partitions.size();
                        }
                    }
                    if(!placed)
                    {
                        for(int i = 0; i < previous; i++)
                        {
                            if(partitions.get(i).size >= adding.size && partitions.get(i).empty)
                            {
                                partitions.get(i).fill(adding);
                                placed = true;
                                frag += partitions.get(i).size - adding.size;
                                previous = i;
                                i += partitions.size();
                            }
                        }
                    }
                    if(!placed)
                    {
                        queue.add(adding);
                    }
                    break;
                } 
                default: // case 3, worst-fit
                {
                    Job adding = new Job(size, nextJobNumber);
                    nextJobNumber++;
                    int worst = -2;
                    for(int i = 0; i < partitions.size(); i++)
                    {
                        if(partitions.get(i).size >= adding.size && partitions.get(i).empty)
                        {
                            if(worst < 0 || partitions.get(i).size > partitions.get(worst).size)
                            {
                                worst = i;
                            }
                        }
                    }
                    if(worst < 0)
                    {
                        queue.add(adding);
                    }
                    else
                    {
                        partitions.get(worst).fill(adding);
                        frag += partitions.get(worst).size - adding.size;
                    }
                    break;
                }
            }
        }
        
        private boolean removeJob(int number)
        {
            for(int i = 0; i < partitions.size(); i++)
            {
                if(partitions.get(i).job != null)
                {
                    if(partitions.get(i).job.name == number)
                    {
                        frag -= partitions.get(i).size - partitions.get(i).job.size;
                        partitions.get(i).empty();
                        searchQueue(i);
                        return true;
                    }
                }
            }
            for(int i = 0; i < queue.size(); i++)
            {
                if(queue.get(i).name == number)
                {
                    queue.remove(i);
                    return true;
                }
            }
            return false;
        }
        
        private void searchQueue(int i) // i is the location of the partition in the list
        {
            switch(allocationMode)
            {
                case 0: // first-fit
                {
                    for(int j = 0; j < queue.size(); j++)
                    {
                        if(queue.get(j).size <= partitions.get(i).size)
                        {
                            partitions.get(i).fill(queue.get(j));
                            frag += partitions.get(i).size - queue.get(j).size;
                            queue.remove(j);
                            j += queue.size();
                        }
                    }
                    break;
                }
                case 1: // best-fit
                {
                    int best = -2;
                    for(int j = 0; j < queue.size(); j++)
                    {
                        if(queue.get(j).size <= partitions.get(i).size)
                        {
                            if(best < 0 || queue.get(j).size > queue.get(best).size)
                            {
                                best = j;
                            }
                        }
                    }
                    if(best >= 0)
                    {
                        partitions.get(i).fill(queue.get(best));
                        frag += partitions.get(i).size - queue.get(best).size;
                        queue.remove(best);
                    }
                    break;
                }
                case 2: // next-fit
                {
                    for(int j = 0; j < queue.size(); j++)
                    {
                        if(queue.get(j).size <= partitions.get(i).size)
                        {
                            partitions.get(i).fill(queue.get(j));
                            frag += partitions.get(i).size - queue.get(j).size;
                            queue.remove(j);
                            j += queue.size();
                        }
                    }
                    break;
                }
                default: // case 3, worst-fit
                {
                    int worst = -2;
                    for(int j = 0; j < queue.size(); j++)
                    {
                        if(queue.get(j).size <= partitions.get(i).size)
                        {
                            if(worst < 0 || queue.get(j).size < queue.get(worst).size)
                            {
                                worst = j;
                            }
                        }
                    }
                    if(worst >= 0)
                    {
                        partitions.get(i).fill(queue.get(worst));
                        frag += partitions.get(i).size - queue.get(worst).size;
                        queue.remove(worst);
                    }
                    break;
                }
            }
        }
    }
    
    private class DynamicList
    {
        private ArrayList<Partition> partitions;
        private ArrayList<Job> queue; // queue for waiting jobs
        private int freeMem; // the amount of memory not in any partitions
        private int previous; // this is used for next-fit
        private int nextJobNumber;
        private int frag; // fragmentation
        
        private DynamicList()
        {
            partitions = new ArrayList<>();
            queue = new ArrayList<>();
            freeMem = 100;
            if(allocationMode == 2)
            {
                previous = 0;
            }
            nextJobNumber = 1;
            frag = 0;
        }
        
        private void addJob(int size)
        {
            switch(allocationMode)
            {
                // first-fit
                case 0:
                {
                    Job adding = new Job(size, nextJobNumber);
                    nextJobNumber++;
                    boolean placed = false;
                    for(int i = 0; i < partitions.size(); i++)
                    {
                        if(partitions.get(i).size == adding.size && partitions.get(i).empty)
                        {
                            partitions.get(i).fill(adding);
                            placed = true;
                            i += partitions.size();
                        }
                        else if(partitions.get(i).size > adding.size && partitions.get(i).empty)
                        {
                            int remaining = partitions.get(i).size - adding.size;
                            partitions.get(i).size = adding.size;
                            partitions.add(i+1, new Partition(partitions.get(i).start + partitions.get(i).size + 1, remaining));
                            partitions.get(i).fill(adding);
                            placed = true;
                            frag -= partitions.get(i).size;
                            i += partitions.size();
                        }
                    }
                    if(!placed && freeMem >= adding.size)
                    {
                        int newStart;
                        if(partitions.size() > 0)
                        {
                            newStart = partitions.get(partitions.size()-1).start + partitions.get(partitions.size()-1).size;
                        }
                        else
                        {
                            newStart = 0;
                        }
                        partitions.add(new Partition(newStart, adding.size, adding));
                        freeMem -= adding.size;
                    }
                    else if(!placed && freeMem < adding.size)
                    {
                        queue.add(adding);
                    }
                    break;
                }
                // best-fit
                case 1:
                {
                    Job adding = new Job(size, nextJobNumber);
                    nextJobNumber++;
                    int best = -2;
                    for(int i = 0; i < partitions.size(); i++)
                    {
                        if(partitions.get(i).size >= adding.size && partitions.get(i).empty)
                        {
                            if(best < 0 || partitions.get(i).size < partitions.get(best).size)
                            {
                                best = i;
                            }
                        }
                    }
                    if(best >= 0)
                    {
                        if(partitions.get(best).size == adding.size)
                        {
                            partitions.get(best).fill(adding);
                        }
                        else if(partitions.get(best).size > adding.size)
                        {
                            int remaining = partitions.get(best).size - adding.size;
                            partitions.get(best).size = adding.size;
                            partitions.add(best+1, new Partition(partitions.get(best).start + partitions.get(best).size + 1, remaining));
                            partitions.get(best).fill(adding);
                            frag -= partitions.get(best).size;
                        }
                    }
                    else if(best < 0 && freeMem >= adding.size)
                    {
                        int newStart;
                        if(partitions.size() > 0)
                        {
                            newStart = partitions.get(partitions.size()-1).start + partitions.get(partitions.size()-1).size;
                        }
                        else
                        {
                            newStart = 0;
                        }
                        partitions.add(new Partition(newStart, adding.size, adding));
                        freeMem -= adding.size;
                    }
                    else
                    {
                        queue.add(adding);
                    }
                    break;
                }
                // next-fit
                case 2:
                {
                    Job adding = new Job(size, nextJobNumber);
                    nextJobNumber++;
                    boolean placed = false;
                    while(previous > partitions.size() && !partitions.isEmpty())
                    {
                        previous--;
                    }
                    for(int i = previous; i < partitions.size(); i++)
                    {
                        if(partitions.get(i).size == adding.size && partitions.get(i).empty)
                        {
                            partitions.get(i).fill(adding);
                            placed = true;
                            previous = i;
                            i += partitions.size();
                        }
                        else if(partitions.get(i).size > adding.size && partitions.get(i).empty)
                        {
                            int remaining = partitions.get(i).size - adding.size;
                            partitions.get(i).size = adding.size;
                            partitions.add(i+1, new Partition(partitions.get(i).start + partitions.get(i).size + 1, remaining));
                            partitions.get(i).fill(adding);
                            placed = true;
                            frag -= partitions.get(i).size;
                            previous = i;
                            i += partitions.size();
                        }
                    }
                    if(!placed)
                    {
                        for(int i = 0; i < previous; i++)
                        {
                            if(partitions.get(i).size == adding.size && partitions.get(i).empty)
                            {
                                partitions.get(i).fill(adding);
                                placed = true;
                                previous = i;
                                i += partitions.size();
                            }
                            else if(partitions.get(i).size > adding.size && partitions.get(i).empty)
                            {
                                int remaining = partitions.get(i).size - adding.size;
                                partitions.get(i).size = adding.size;
                                partitions.add(i+1, new Partition(partitions.get(i).start + partitions.get(i).size + 1, remaining));
                                partitions.get(i).fill(adding);
                                placed = true;
                                frag -= partitions.get(i).size;
                                previous = i;
                                i += partitions.size();
                            }
                        }
                    }
                    if(!placed && freeMem >= adding.size)
                    {
                        int newStart;
                        if(partitions.size() > 0)
                        {
                            newStart = partitions.get(partitions.size()-1).start + partitions.get(partitions.size()-1).size;
                        }
                        else
                        {
                            newStart = 0;
                        }
                        partitions.add(new Partition(newStart, adding.size, adding));
                        previous = partitions.size()-1;
                        freeMem -= adding.size;
                    }
                    else if(!placed && freeMem < adding.size)
                    {
                        queue.add(adding);
                    }
                    break;
                }
                // case 3, worst-fit
                default:
                {
                    Job adding = new Job(size, nextJobNumber);
                    nextJobNumber++;
                    int worst = -2;
                    for(int i = 0; i < partitions.size(); i++)
                    {
                        if(partitions.get(i).size >= adding.size && partitions.get(i).empty)
                        {
                            if(worst < 0 || partitions.get(i).size < partitions.get(worst).size)
                            {
                                worst = i;
                            }
                        }
                    }
                    if(worst >= 0)
                    {
                        if(partitions.get(worst).size == adding.size)
                        {
                            partitions.get(worst).fill(adding);
                        }
                        else if(partitions.get(worst).size > adding.size)
                        {
                            int remaining = partitions.get(worst).size - adding.size;
                            partitions.get(worst).size = adding.size;
                            partitions.add(worst+1, new Partition(partitions.get(worst).start + partitions.get(worst).size + 1, remaining));
                            partitions.get(worst).fill(adding);
                            frag -= partitions.get(worst).size;
                        }
                    }
                    else if(worst < 0 && freeMem >= adding.size)
                    {
                        int newStart;
                        if(partitions.size() > 0)
                        {
                            newStart = partitions.get(partitions.size()-1).start + partitions.get(partitions.size()-1).size;
                        }
                        else
                        {
                            newStart = 0;
                        }
                        partitions.add(new Partition(newStart, adding.size, adding));
                        freeMem -= adding.size;
                    }
                    else
                    {
                        queue.add(adding);
                    }
                    break;
                }
            }
        }
        
        private boolean removeJob(int number)
        {
            for(int i = 0; i < partitions.size(); i++)
            {
                if(partitions.get(i).job != null)
                {
                    if(partitions.get(i).job.name == number)
                    {
                        frag += partitions.get(i).size;
                        partitions.get(i).empty();
                        if(i+1 < partitions.size() && partitions.get(i+1).empty)
                        {
                            partitions.get(i).size += partitions.get(i+1).size;
                            partitions.remove(i+1);
                        }
                        if(i-1 >= 0 && partitions.get(i-1).empty)
                        {
                            partitions.get(i).size += partitions.get(i-1).size;
                            partitions.get(i).start = partitions.get(i-1).start;
                            partitions.remove(i-1);
                            i--;
                        }
                        if(i == partitions.size()-1)
                        {
                            freeMem += partitions.get(i).size;
                            frag -= partitions.get(i).size;
                            partitions.remove(i);
                        }
                        if(i < partitions.size())
                        {
                            searchQueue(i);
                        }
                        searchQueue();
                        return true;
                    }
                }
            }
            for(int i = 0; i < queue.size(); i++)
            {
                if(queue.get(i).name == number)
                {
                    queue.remove(i);
                    return true;
                }
            }
            return false;
        }
        
        private void searchQueue(int i) // i is the location of the partition in the list
        {
            switch(allocationMode)
            {
                case 0: // first-fit
                {
                    for(int j = 0; j < queue.size(); j++)
                    {
                        if(queue.get(j).size == partitions.get(i).size)
                        {
                            partitions.get(i).fill(queue.get(j));
                            frag -= queue.get(j).size;
                            queue.remove(j);
                            j += queue.size();
                        }
                        else if(queue.get(j).size < partitions.get(i).size)
                        {
                            int remaining = partitions.get(i).size - queue.get(j).size;
                            partitions.get(i).size = queue.get(j).size;
                            partitions.add(i+1, new Partition(partitions.get(i).start + partitions.get(i).size + 1, remaining));
                            partitions.get(i).fill(queue.get(j));
                            frag -= queue.get(j).size;
                            queue.remove(j);
                            j += queue.size();
                        }
                    }
                    break;
                }
                case 1: // best-fit
                {
                    int best = -2;
                    for(int j = 0; j < queue.size(); j++)
                    {
                        if(queue.get(j).size <= partitions.get(i).size)
                        {
                            if(best < 0 || queue.get(j).size > queue.get(best).size)
                            {
                                best = j;
                            }
                        }
                    }
                    if(best >= 0 && partitions.get(i).size == queue.get(best).size)
                    {
                        partitions.get(i).fill(queue.get(best));
                        frag -= queue.get(best).size;
                        queue.remove(best);
                    }
                    else if(best >= 0 && partitions.get(i).size > queue.get(best).size)
                    {
                        int remaining = partitions.get(i).size - queue.get(best).size;
                        partitions.get(i).size = queue.get(best).size;
                        partitions.add(i+1, new Partition(partitions.get(i).start + partitions.get(i).size + 1, remaining));
                        partitions.get(i).fill(queue.get(best));
                        frag -= queue.get(best).size;
                        queue.remove(best);
                    }
                    break;
                }
                case 2: // next-fit
                {
                    for(int j = 0; j < queue.size(); j++)
                    {
                        if(queue.get(j).size == partitions.get(i).size)
                        {
                            partitions.get(i).fill(queue.get(j));
                            frag -= queue.get(j).size;
                            queue.remove(j);
                            j += queue.size();
                        }
                        else if(queue.get(j).size > partitions.get(i).size)
                        {
                            int remaining = partitions.get(i).size - queue.get(j).size;
                            partitions.get(i).size = queue.get(j).size;
                            partitions.add(i+1, new Partition(partitions.get(i).start + partitions.get(i).size + 1, remaining));
                            partitions.get(i).fill(queue.get(j));
                            frag -= queue.get(j).size;
                            queue.remove(j);
                            j += queue.size();
                        }
                    }
                    break;
                }
                default: // case 3, worst-fit
                {
                    int worst = -2;
                    for(int j = 0; j < queue.size(); j++)
                    {
                        if(queue.get(j).size <= partitions.get(i).size)
                        {
                            if(worst < 0 || queue.get(j).size < queue.get(worst).size)
                            {
                                worst = j;
                            }
                        }
                    }
                    if(worst >= 0 && partitions.get(i).size == queue.get(worst).size)
                    {
                        partitions.get(i).fill(queue.get(worst));
                        frag -= queue.get(worst).size;
                        queue.remove(worst);
                    }
                    else if(worst >= 0 && partitions.get(i).size > queue.get(worst).size)
                    {
                        int remaining = partitions.get(i).size - queue.get(worst).size;
                        partitions.get(i).size = queue.get(worst).size;
                        partitions.add(i+1, new Partition(partitions.get(i).start + partitions.get(i).size + 1, remaining));
                        partitions.get(i).fill(queue.get(worst));
                        frag -= queue.get(worst).size;
                        queue.remove(worst);
                    }
                    break;
                }
            }
        }
        
        private void searchQueue() // searches to see if job can fit into free memory
        {
            switch(allocationMode)
            {
                case 0: // first-fit
                {
                    for(int j = 0; j < queue.size(); j++)
                    {
                        if(queue.get(j).size <= freeMem)
                        {
                            int newPartitionStart;
                            if(partitions.isEmpty())
                            {
                                newPartitionStart = 0;
                            }
                            else
                            {
                                newPartitionStart = partitions.get(partitions.size()-1).start + partitions.get(partitions.size()-1).size + 1;
                            }
                            partitions.add(new Partition(newPartitionStart, queue.get(j).size, queue.get(j)));
                            freeMem -= queue.get(j).size;
                            queue.remove(j);
                            j += queue.size();
                        }
                    }
                    break;
                }
                case 1: // best-fit
                {
                    int best = -2;
                    for(int j = 0; j < queue.size(); j++)
                    {
                        if(queue.get(j).size <= freeMem)
                        {
                            if(best < 0 || queue.get(j).size > queue.get(best).size)
                            {
                                best = j;
                            }
                        }
                    }
                    if(best >= 0 && freeMem >= queue.get(best).size)
                    {
                        int newPartitionStart;
                        if(partitions.isEmpty())
                        {
                            newPartitionStart = 0;
                        }
                        else
                        {
                            newPartitionStart = partitions.get(partitions.size()-1).start + partitions.get(partitions.size()-1).size + 1;
                        }
                        partitions.add(new Partition(newPartitionStart, queue.get(best).size, queue.get(best)));
                        freeMem -= queue.get(best).size;
                        queue.remove(best);
                    }
                    break;
                }
                case 2: // next-fit
                {
                    for(int j = 0; j < queue.size(); j++)
                    {
                        if(queue.get(j).size <= freeMem)
                        {
                            int newPartitionStart;
                            if(partitions.isEmpty())
                            {
                                newPartitionStart = 0;
                            }
                            else
                            {
                                newPartitionStart = partitions.get(partitions.size()-1).start + partitions.get(partitions.size()-1).size + 1;
                            }
                            partitions.add(new Partition(newPartitionStart, queue.get(j).size, queue.get(j)));
                            freeMem -= queue.get(j).size;
                            queue.remove(j);
                            j += queue.size();
                        }
                    }
                    break;
                }
                default: // case 3, worst-fit
                {
                    int worst = -2;
                    for(int j = 0; j < queue.size(); j++)
                    {
                        if(queue.get(j).size <= freeMem)
                        {
                            if(worst < 0 || queue.get(j).size < queue.get(worst).size)
                            {
                                worst = j;
                            }
                        }
                    }
                    if(worst >= 0 && freeMem >= queue.get(worst).size)
                    {
                        int newPartitionStart;
                        if(partitions.isEmpty())
                        {
                            newPartitionStart = 0;
                        }
                        else
                        {
                            newPartitionStart = partitions.get(partitions.size()-1).start + partitions.get(partitions.size()-1).size + 1;
                        }
                        partitions.add(new Partition(newPartitionStart, queue.get(worst).size, queue.get(worst)));
                        freeMem -= queue.get(worst).size;
                        queue.remove(worst);
                    }
                    break;
                }
            }
        }
    }
    
    private class Partition
    {
        private int start;
        private int size;
        private Job job;
        private boolean empty;
        
        // constructs empty partiton
        private Partition(int inputStart, int inputSize)
        {
            start = inputStart;
            size = inputSize;
            empty = true;
        }
        
        // constructs filled partiton
        private Partition(int inputStart, int inputSize, Job inputJob)
        {
            start = inputStart;
            size = inputSize;
            job = inputJob;
            empty = false;
        }
        
        private void fill(Job add)
        {
            job = add;
            empty = false;
        }
        
        private void empty()
        {
            job = null;
            empty = true;
        }
    }
    
    private class Job
    {
        private int size;
        private final int name;
        
        private Job(int sizeInput, int inputName)
        {
            size = sizeInput;
            name = inputName;
        }
    }
    
    private class GUI implements ActionListener
    {
        GridBagConstraints c;
        
        JFrame frame;
        
        JPanel setup;
        
        JLabel memSizeLabel;
        JFormattedTextField memSizeInput;
        JLabel memSizeCurrent;
        JLabel addPartition;
        JFormattedTextField addPartitionInput;
        JLabel memLeft;
        JLabel partitionList;
        JLabel allocationLabel;
        JRadioButton first;
        JRadioButton best;
        JRadioButton next;
        JRadioButton worst;
        ButtonGroup allocation;
        JButton start;
        
        JPanel simulation;
        
        JLabel addRemoveLabel;
        JFormattedTextField addRemoveInput;
        JButton addJob;
        JButton removeJob;
        JLabel addRemoveSuccess;
        JLabel fixedSnapLabel;
        JLabel dynamicSnapLabel;
        JTable fixedSnap;
        JTable fixedQueue;
        JTable dynamicSnap;
        JTable dynamicQueue;
        JLabel fixedTableLabel;
        JLabel dynamicTableLabel;
        JTable fixedTable;
        JTable dynamicTable;
        JLabel fixedFrag;
        JLabel dynamicFrag;
        
        private GUI()
        {
            c = new GridBagConstraints();
            
            frame = new JFrame();
            
            memSizeLabel = new JLabel("Enter memory size:");
            memSizeInput = new JFormattedTextField();
            memSizeInput.setValue(int.class);
            memSizeInput.addActionListener(this);
            memSizeCurrent = new JLabel("Current memory size: " + memSize);
            addPartition = new JLabel("Enter size for new partition for fixed allocation:");
            addPartitionInput = new JFormattedTextField();
            addPartitionInput.setValue(int.class);
            addPartitionInput.addActionListener(this);
            partitionList = new JLabel("Current partitions:");
            memLeft = new JLabel("Memory left: " + fixedList.memRemaining);
            allocationLabel = new JLabel("Select of the the following allocation techniques:");
            first = new JRadioButton("First-Fit", true);
            first.addActionListener(this);
            best = new JRadioButton("Best-Fit");
            best.addActionListener(this);
            next = new JRadioButton("Next-Fit");
            next.addActionListener(this);
            worst = new JRadioButton("Worst-Fit");
            worst.addActionListener(this);
            allocation = new ButtonGroup();
            allocation.add(first);
            allocation.add(best);
            allocation.add(next);
            allocation.add(worst);
            start = new JButton("Start simulation");
            start.addActionListener(this);
            
            setup = new JPanel();
            setup.setBorder(BorderFactory.createEmptyBorder(200,100,200,50));
            setup.setLayout(new GridBagLayout());
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 0;
            c.weightx = 0.5;
            setup.add(memSizeLabel,c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 0;
            c.weightx = 0.5;
            setup.add(memSizeInput, c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 0;
            c.weightx = 0.5;
            setup.add(memSizeCurrent, c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 1;
            c.weightx = 0.5;
            setup.add(addPartition, c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 1;
            c.weightx = 0.5;
            setup.add(addPartitionInput, c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 1;
            c.weightx = 0.5;
            setup.add(memLeft, c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 2;
            c.weightx = 0.5;
            setup.add(partitionList, c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 3;
            c.weightx = 0.5;
            setup.add(allocationLabel, c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 4;
            c.weightx = 0.5;
            setup.add(first, c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 1;
            c.gridy = 4;
            c.weightx = 0.5;
            setup.add(best, c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 2;
            c.gridy = 4;
            c.weightx = 0.5;
            setup.add(next, c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 3;
            c.gridy = 4;
            c.weightx = 0.5;
            setup.add(worst, c);
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridx = 0;
            c.gridy = 5;
            c.weightx = 0.5;
            setup.add(start, c);
            
            
            frame.add(setup, BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        }
        
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource() == memSizeInput)
            {
                String input = memSizeInput.getText();
                try
                {
                    memSize = Integer.parseInt(input);
                    memSizeCurrent.setText("Current memory size: " + memSize);
                    fixedList.memRemaining = memSize;
                    fixedList.partitions.clear();
                    dynamicList.freeMem = memSize;
                    partitionList.setText("Current partitions: ");
                    memLeft.setText("Memory left: " + fixedList.memRemaining);
                }
                catch(NumberFormatException f)
                {
                    memSize = 100;
                }
            }
            else if(e.getSource() == addPartitionInput)
            {
                try
                {
                    int input = Integer.parseInt(addPartitionInput.getText());
                    if(input <= fixedList.memRemaining && input > 0)
                    {
                        int newStart;
                        if(fixedList.partitions.size() > 0)
                        {
                            newStart = fixedList.partitions.get(fixedList.partitions.size()-1).start + fixedList.partitions.get(fixedList.partitions.size()-1).size;
                        }
                        else
                        {
                            newStart = 0;
                        }
                        fixedList.partitions.add(new Partition(newStart, input));
                        fixedList.memRemaining -= input;
                    }
                    String partitions = "";
                    for(int i = 0; i < fixedList.partitions.size(); i++) 
                    {
                        partitions += fixedList.partitions.get(i).size;
                        if(i < fixedList.partitions.size()-1)
                        {
                            partitions += ", ";
                        }
                    }
                    partitionList.setText("Current partitions: " + partitions);
                    memLeft.setText("Memory left: " + fixedList.memRemaining);
                }
                catch(NumberFormatException f)
                {
                    // nothing happens
                }
            }
            else if(e.getSource() == first)
            {
                allocationMode = 0;
            }
            else if(e.getSource() == best)
            {
                allocationMode = 1;
            }
            else if(e.getSource() == next)
            {
                allocationMode = 2;
            }
            else if(e.getSource() == worst)
            {
                allocationMode = 3;
            }
            else if(e.getSource() == start)
            {
                if(fixedList.memRemaining > 0)
                {
                    int newStart;
                    if(fixedList.partitions.size() > 0)
                    {
                        newStart = fixedList.partitions.get(fixedList.partitions.size()-1).start + fixedList.partitions.get(fixedList.partitions.size()-1).size;
                    }
                    else
                    {
                        newStart = 0;
                    }
                    fixedList.partitions.add(new Partition(newStart, fixedList.memRemaining));
                }
                
                addRemoveLabel = new JLabel("Enter the size of the new job or the number of the job to remove:");
                addRemoveInput = new JFormattedTextField();
                addRemoveInput.setValue(int.class);
                addJob = new JButton("Add Job");
                addJob.addActionListener(this);
                removeJob = new JButton("Remove Job");
                removeJob.addActionListener(this);
                addRemoveSuccess = new JLabel();
                fixedSnapLabel = new JLabel("Fixed Snapshot");
                dynamicSnapLabel = new JLabel("Dynamic Snapshot");
                fixedSnap = snapshotMaker(fixedList.partitions, true);
                fixedQueue = queueMaker(fixedList.queue, true);
                dynamicSnap = snapshotMaker(dynamicList.partitions, false);
                dynamicQueue = queueMaker(dynamicList.queue, false);
                fixedTableLabel = new JLabel("Fixed Partition Information");
                dynamicTableLabel = new JLabel("Dynamic Internal Fragentation");
                fixedTable = fixedPartitionTable();
                dynamicTable = dynamicPartitionTable();
                fixedFrag = new JLabel("Fixed Fragmentation: 0");
                dynamicFrag = new JLabel("Dynamic Fragmentation: 0");
                
                
                simulation = new JPanel();
                simulation.setBorder(BorderFactory.createEmptyBorder(200,100,200,50));
                simulation.setLayout(new GridBagLayout());
                c.fill = GridBagConstraints.HORIZONTAL;
                c.gridx = 2;
                c.gridy = 0;
                simulation.add(addRemoveLabel, c);
                c.gridx = 1;
                c.gridy = 1;
                simulation.add(addRemoveSuccess, c);
                c.gridx = 2;
                simulation.add(addRemoveInput, c);
                c.gridx = 3;
                simulation.add(addJob, c);
                c.gridx = 4;
                simulation.add(removeJob, c);
                c.gridx = 0;
                c.gridy = 2;
                simulation.add(fixedSnapLabel, c);
                c.gridx = 3;
                simulation.add(dynamicSnapLabel, c);
                c.gridx = 0;
                c.gridy = 3;
                simulation.add(fixedSnap, c);
                c.gridx = 1;
                simulation.add(fixedQueue, c);
                c.gridx = 2;
                simulation.add(new JLabel(), c); // for formatting
                c.gridx = 3;
                simulation.add(dynamicSnap, c);
                c.gridx = 4;
                simulation.add(dynamicQueue, c);
                c.gridx = 0;
                c.gridy = 4;
                simulation.add(fixedTableLabel, c);
                c.gridx = 3;
                simulation.add(dynamicTableLabel, c);
                c.gridx = 0;
                c.gridy = 5;
                simulation.add(fixedTable, c);
                c.gridx = 3;
                simulation.add(dynamicTable, c);
                c.gridx = 2;
                c.gridy = 6;
                simulation.add(fixedFrag, c);
                c.gridy = 7;
                simulation.add(dynamicFrag, c);
                
                frame.remove(setup);
                frame.validate();
                frame.repaint();
                frame.add(simulation, BorderLayout.CENTER);
                frame.validate();
                frame.repaint();
            }
            else if(e.getSource() == addJob)
            {
                try
                {
                    int input = Integer.parseInt(addRemoveInput.getText());
                    if(input > 0)
                    {
                        fixedList.addJob(input);
                        dynamicList.addJob(input);
                        addRemoveSuccess.setText("Success");
                        fixedFrag.setText("Fixed Fragmentation: " + fixedList.frag);
                        dynamicFrag.setText("Dynamic Fragmentation: " + dynamicList.frag);
                        resetSimulationPanel();
                    }
                    else
                    {
                        addRemoveSuccess.setText("Failed: not positive");
                    }
                }
                catch(NumberFormatException f)
                {
                    addRemoveSuccess.setText("Failed: not integer");
                }
            }
            else if(e.getSource() == removeJob)
            {
                try
                {
                    int input = Integer.parseInt(addRemoveInput.getText());
                    if(input > 0)
                    {
                        boolean fixedRemoved = fixedList.removeJob(input);
                        boolean dynamicRemoved = dynamicList.removeJob(input);
                        if(fixedRemoved && dynamicRemoved)
                        {
                            addRemoveSuccess.setText("Success");
                        }
                        else if(!fixedRemoved && !dynamicRemoved)
                        {
                            addRemoveSuccess.setText("Failed: no such job");
                        }
                        else // the user should NEVER get this, because it shouuld remove from both
                        {
                            addRemoveSuccess.setText("ERROR: only in one list");
                        }
                        fixedFrag.setText("Fixed Fragmentation: " + fixedList.frag);
                        dynamicFrag.setText("Dynamic Fragmentation: " + dynamicList.frag);
                        resetSimulationPanel();
                    }
                    else
                    {
                        addRemoveSuccess.setText("Failed: not positive");
                    }
                }
                catch(NumberFormatException f)
                {
                    addRemoveSuccess.setText("Failed: not integer");
                }
            }
        }
        
        private void resetSimulationPanel()
        {
            simulation.remove(fixedSnap);
            simulation.remove(fixedTable);
            simulation.remove(fixedQueue);
            simulation.remove(dynamicSnap);
            simulation.remove(dynamicTable);
            simulation.remove(dynamicQueue);
            
            frame.validate();
            frame.repaint();
            
            fixedSnap = snapshotMaker(fixedList.partitions, true);
            fixedTable = fixedPartitionTable();
            fixedQueue = queueMaker(fixedList.queue, true);
            dynamicSnap = snapshotMaker(dynamicList.partitions, false);
            dynamicTable = dynamicPartitionTable();
            dynamicQueue = queueMaker(dynamicList.queue, false);
            
            c.gridx = 0;
            c.gridy = 3;
            simulation.add(fixedSnap, c);
            c.gridx = 1;
            simulation.add(fixedQueue, c);
            c.gridx = 3;
            simulation.add(dynamicSnap, c);
            c.gridx = 4;
            simulation.add(dynamicQueue, c);
            c.gridx = 0;
            c.gridy = 5;
            simulation.add(fixedTable, c);
            c.gridx = 3;
            simulation.add(dynamicTable, c);
            
            frame.validate();
            frame.repaint();
        }
        
        private JTable snapshotMaker(ArrayList<Partition> list, boolean forFixedList)
        {
            JTable snapshot = new JTable(list.size()+1, 1);
            if(forFixedList)
            {
                snapshot.setValueAt("Fixed List", 0, 0);
            }
            else //for dynamic list
            {
                snapshot.setValueAt("Dynamic List", 0, 0);
            }
            for(int i = 0; i < list.size(); i++)
            {
                String value;
                if(list.get(i).empty)
                {
                    value = "empty";
                }
                else // not empty
                {
                    value = "Job #" + list.get(i).job.name + " (" + list.get(i).job.size + ")";
                }
                snapshot.setValueAt(value, i+1, 0);
            }
            return snapshot;
        }
        
        private JTable queueMaker(ArrayList<Job> list, boolean forFixedList)
        {
            JTable queue = new JTable(list.size()+1, 1);
            if(forFixedList)
            {
                queue.setValueAt("Fixed Queue", 0, 0);
            }
            else //for dynamic list
            {
                queue.setValueAt("Dynamic Queue", 0, 0);
            }
            for(int i = 0; i < list.size(); i++)
            {
                String value = "Job #" + list.get(i).name + " (" + list.get(i).size + ")";
                queue.setValueAt(value, i+1, 0);
            }
            return queue;
        }
        
        private JTable fixedPartitionTable()
        {
            JTable table = new JTable(fixedList.partitions.size()+1, 4);
            table.setValueAt("Partition Size", 0, 0);
            table.setValueAt("Memory Address", 0, 1);
            table.setValueAt("Access", 0, 2);
            table.setValueAt("Partition Status", 0, 3);
            for(int i = 0; i < fixedList.partitions.size(); i++)
            {
                table.setValueAt(fixedList.partitions.get(i).size, i+1, 0);
                table.setValueAt(fixedList.partitions.get(i).start, i+1, 1);
                if(fixedList.partitions.get(i).empty)
                {
                    table.setValueAt("", i+1, 2);
                    table.setValueAt("Free", i+1, 3);
                }
                else // not empty
                {
                    table.setValueAt("Job #" + fixedList.partitions.get(i).job.name, i+1, 2);
                    table.setValueAt("Busy", i+1, 3);
                }
            }
            return table;
        }
        
        private JTable dynamicPartitionTable()
        {
            int numberOfEmpty = 0;
            for(int i = 0; i < dynamicList.partitions.size(); i++)
            {
                if(dynamicList.partitions.get(i).empty)
                {
                    numberOfEmpty++;
                }
            }
            JTable table = new JTable(numberOfEmpty+1, 2);
            table.setValueAt("Beginning Address", 0, 0);
            table.setValueAt("Free Memory Block Size", 0, 1);
            int row = 1;
            for(int i = 0; i < dynamicList.partitions.size(); i++)
            {
                if(dynamicList.partitions.get(i).empty)
                {
                    table.setValueAt(dynamicList.partitions.get(i).start, row, 0);
                    table.setValueAt(dynamicList.partitions.get(i).size, row, 1);
                    row++;
                }
            }
            return table;
        }
    }
    
    public static void main(String[] args)
    {
        MemorySimulation run = new MemorySimulation();
    }
}