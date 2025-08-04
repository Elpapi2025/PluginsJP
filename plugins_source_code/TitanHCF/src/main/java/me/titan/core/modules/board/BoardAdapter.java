package me.titan.core.modules.board;

import org.bukkit.entity.*;
import java.util.*;

public interface BoardAdapter {
    List<String> getLines(Player p0);
    
    String getTitle(Player p0);
}