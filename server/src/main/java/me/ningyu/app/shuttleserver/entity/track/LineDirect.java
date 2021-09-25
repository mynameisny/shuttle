package me.ningyu.app.shuttleserver.entity.track;

public enum LineDirect
{
    FORWARD(1),     // 去程
    BACKWARD(2);    // 返程
    
    private int value;
    
    LineDirect(int value)
    {
        this.value = value;
    }
}
