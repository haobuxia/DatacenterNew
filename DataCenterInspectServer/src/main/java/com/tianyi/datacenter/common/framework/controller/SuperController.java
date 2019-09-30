/*
 * Copyright (c) 2018-2022 Caratacus, (caratacus@qq.com).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package com.tianyi.datacenter.common.framework.controller;

import com.tianyi.datacenter.common.vo.ResponseVo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;

/**
 * SuperController
 *
 * @author lielele
 */
public class SuperController {

    protected Logger logger = LogManager.getLogger(getClass());
    /**
     * 成功返回
     *
     * @return
     */
    public ResponseVo success() {
        ResponseVo responseVo = new ResponseVo();
        return responseVo.success();
    }
    /**
     * 成功返回
     *
     * @return
     */
    public ResponseVo success(Map data) {
        ResponseVo responseVo = new ResponseVo();
        return responseVo.success(data);
    }
    /**
     * 成功返回
     *
     * @return
     */
    public ResponseVo success(Map data,String message) {
        ResponseVo responseVo = new ResponseVo();
        return responseVo.success(data,message);
    }

    /**
     * 失败返回
     */
    public ResponseVo fail(String message) {
        ResponseVo responseVo = new ResponseVo();
        return responseVo.fail(message);
    }
    /**
     * 失败返回
     */
    public ResponseVo fail(Map data,String message) {
        ResponseVo responseVo = new ResponseVo();
        return responseVo.fail(message,data);
    }
}
