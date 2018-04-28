<?xml version="1.0" encoding="UTF-8"?>

<!--
    Document   : styleSheet.xsl
    Created on : January 17, 2018, 10:03 PM
    Author     : Nouran
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="html"/>

    <!-- TODO customize transformation rules 
         syntax recommendation http://www.w3.org/TR/xslt 
    -->
    <xsl:template match="/chat">
        <html>
            <head>
                <style>
                    
                    .container1 {    
                    background-color: #619bf9 ; 
                    border-radius: 20px;
                    padding: 0 10 0 10;
                    margin: 0 10 0 10;
                    border-insets: 2px;
                    background-insets: 2px;
                    }
                      
                    
                    .container2 {
                    background-color: #bbbec1 ; 
                    border-radius: 20px;
                    padding: 0 10 0 10;
                    margin: 0 10 0 10;
                    border-insets: 2px;
                    background-insets: 2px;
                    }
                    
                    
                    .chat {
                    list-style: none;
                    background: none;
                    margin: 0;
                    padding: 50px 0 50px 0;
                    margin-top: 60px;
                    margin-bottom: 10px;
                    }
                    
                    .chat li {
                    padding: 0.2rem;
                    overflow: hidden;
                    display: flex;
                    }
                    
                    
                    .self {
                    justify-content: flex-end;
                    align-items: flex-end;
                    }                   
                   
                  
                    .main {
                    background: #f7f7e3;
                    margin:10 500 10 500;
                    height:auto;
                    border-radius: 20px;
                    }
                    
  
                    .label {
                    margin: 5 5 5 5 ;
                    padding: 10 1 10 1  ;
                    }
                    
                    .mainlabel {    
                    border-style: solid;
                    border-width: 1px;
                    border-color: #1daf8d;
                    background-color: #f7f7e3;
                    color: #1daf8d ; 
                    text-align: center;
                    vertical-align: middle;
                    border-radius: 10px;
                    padding: 10 10 10 10;
                    margin: 0 30 50 30;
                    border-insets: 2px;
                    background-insets: 2px;
                    width: 100%;
                    height: 20;
                    }
                    
                    
                    
                </style>
            </head>
            <body>
                <div class="main" >
                    <ol class="chat">
                        <li> 
                            <div class="mainlabel"> 
                                <xsl:value-of select="chatWith" />
                            </div>
                        </li>
                        <xsl:for-each select="message">
                            <xsl:choose>
                                <xsl:when test="direction='left'">
                                    <li class= "other"> 
                                        <div class="container2" align="{direction}"> 
                                            <label class="label"
                                                   style="
                                                     color:{color};">
                                                <xsl:value-of select="messageBody" />
                                            </label> 
                                        </div>
                                    </li>
                                </xsl:when>
                                <xsl:otherwise>
                                    <li class="self">
                                        <div class="container1" align="{direction}">
                                            <label class="label" style="
                                                    color:{color};">
                                                <xsl:value-of select="messageBody" />
                                            </label>
                                        </div>
                                    </li>
                        
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:for-each>
                    </ol>
                </div>
            </body>
        </html>
    </xsl:template>
    
</xsl:stylesheet>