package com.wydpp.gb28181.commander;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

/**    
 * @description:设备能力接口，用于定义设备的控制、查询能力   
 * @author: swwheihei
 * @date:   2020年5月3日 下午9:22:48     
 */
@Component
@DependsOn("sipLayer")
public class SIPCommander implements ISIPCommander {

}
