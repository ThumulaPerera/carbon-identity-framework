/*
 * Copyright (c) 2021, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.wso2.carbon.identity.claim.metadata.mgt.dto.res;

import com.hazelcast.com.fasterxml.jackson.annotation.JsonProperty;

/**
* Claim dialect response.
**/
public class ClaimDialectResDTO {

private String id = null;

private String dialectURI = null;

private LinkDTO link = null;

/**
* Dialect id.
**/
@JsonProperty("id")
public String getId() {
    return id;
}
public void setId(String id) {
    this.id = id;
}

/**
* URI of the claim dialect.
**/
@JsonProperty("dialectURI")
public String getDialectURI() {
    return dialectURI;
}
public void setDialectURI(String dialectURI) {
    this.dialectURI = dialectURI;
}

/**
**/
@JsonProperty("link")
public LinkDTO getLink() {
    return link;
}
public void setLink(LinkDTO link) {
    this.link = link;
}

@Override
public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("class ClaimDialectResDTO {\n");

    sb.append("    id: ").append(id).append("\n");
    sb.append("    dialectURI: ").append(dialectURI).append("\n");
    sb.append("    link: ").append(link).append("\n");

    sb.append("}\n");
    return sb.toString();
}
}
