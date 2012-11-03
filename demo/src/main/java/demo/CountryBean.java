/*
 * $Id: CountryBean.java,v 1.2 2007/12/11 04:14:14 jacky Exp $
 *
 * Copyright (C) 2006 Operamasks Community.
 * Copyright (C) 2000-2006 Apusic Systems, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses.
 */
package demo;

import javax.faces.model.SelectItem;
import javax.faces.event.ValueChangeEvent;

import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;

import java.util.List;
import java.util.ArrayList;

@ManagedBean(name="Country", scope= ManagedBeanScope.SESSION)
public class CountryBean
{
    private String prefix;

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    private String[] ALL_COUNTRIES = {
        "af", "Afghanistan",
        "al", "Albania",
        "dz", "Algeria",
        "as", "American Samoa",
        "ad", "Andorra",
        "ao", "Angola",
        "ai", "Anguilla",
        "aq", "Antartica",
        "ag", "Antigua & Barbuda",
        "ar", "Argentina",
        "am", "Armenia",
        "aw", "Aruba",
        "ac", "Ascension Island",
        "au", "Australia",
        "at", "Austria",
        "az", "Azerbaijan",
        "bs", "Bahamas",
        "bh", "Bahrain",
        "bd", "Bangladesh",
        "bb", "Barbados",
        "by", "Belarus",
        "be", "Belgium",
        "bz", "Belize",
        "bj", "Benin",
        "bm", "Bermuda",
        "bt", "Bhutan",
        "bo", "Bolivia",
        "ba", "Bosnia and Herzegovina",
        "bw", "Botswana",
        "bv", "Bouvet Island",
        "br", "Brazil",
        "io", "British Indian Ocean Territory",
        "bn", "Brunei Darussalam",
        "bg", "Bulgaria",
        "bf", "Burkina Faso",
        "bi", "Burundi",
        "kh", "Cambodia",
        "cm", "Cameroon",
        "ca", "Canada",
        "cv", "Cape Verde",
        "ky", "Cayman Islands",
        "cf", "Central African Republic",
        "td", "Chad",
        "cl", "Chile",
        "cn", "China",
        "cx", "Christmas Island",
        "cc", "Cocos (Keeling) Islands",
        "co", "Colombia",
        "km", "Comoros",
        "cd", "Congo, Democratic Republic of the",
        "cg", "Congo, Republic of",
        "ck", "Cook Islands",
        "cr", "Costa Rica",
        "ci", "Cote d'Ivoire",
        "hr", "Croatia/Hrvatska",
        "cy", "Cyprus",
        "cz", "Czech Republic",
        "dk", "Denmark",
        "dj", "Djibouti",
        "dm", "Dominica",
        "do", "Dominican Republic",
        "tp", "East Timor",
        "ec", "Ecuador",
        "eg", "Egypt",
        "sv", "El Salvador",
        "gq", "Equatorial Guinea",
        "er", "Eritrea",
        "ee", "Estonia",
        "et", "Ethiopia",
        "fk", "Falkland Islands (Malvina)",
        "fo", "Faroe Islands",
        "fj", "Fiji",
        "fi", "Finland",
        "fr", "France",
        "fx", "France (Metropolitan)",
        "gf", "French Guiana",
        "pf", "French Polynesia",
        "tf", "French Southern Territories",
        "ga", "Gabon",
        "gm", "Gambia",
        "ge", "Georgia",
        "de", "Germany",
        "gh", "Ghana",
        "gi", "Gibraltar",
        "gr", "Greece",
        "gl", "Greenland",
        "gd", "Grenada",
        "gp", "Guadeloupe",
        "gu", "Guam",
        "gt", "Guatemala",
        "gg", "Guernsey",
        "gn", "Guinea",
        "gw", "Guinea-Bissau",
        "gy", "Guyana",
        "ht", "Haiti",
        "hm", "Heard and McDonald Islands",
        "va", "Holy See (City Vatican State)",
        "hn", "Honduras",
        "hk", "Hong Kong",
        "hu", "Hungary",
        "is", "Iceland",
        "in", "India",
        "id", "Indonesia",
        "ie", "Ireland",
        "im", "Isle of Man",
        "il", "Israel",
        "it", "Italy",
        "jm", "Jamaica",
        "jp", "Japan",
        "je", "Jersey",
        "jo", "Jordan",
        "kz", "Kazakhstan",
        "ke", "Kenya",
        "ki", "Kiribati",
        "kr", "Korea, Republic of",
        "kw", "Kuwait",
        "kg", "Kyrgyzstan",
        "la", "Lao People's Democratic Republic",
        "lv", "Latvia",
        "lb", "Lebanon",
        "ls", "Lesotho",
        "lr", "Liberia",
        "li", "Liechtenstein",
        "lt", "Lithuania",
        "lu", "Luxembourg",
        "mo", "Macau",
        "mk", "Macedonia, Former Yugoslav Republic",
        "mg", "Madagascar",
        "mw", "Malawi",
        "my", "Malaysia",
        "mv", "Maldives",
        "ml", "Mali",
        "mt", "Malta",
        "mh", "Marshall Islands",
        "mq", "Martinique",
        "mr", "Mauritania",
        "mu", "Mauritius",
        "yt", "Mayotte",
        "mx", "Mexico",
        "fm", "Micronesia, Federal State of",
        "md", "Moldova, Republic of",
        "mc", "Monaco",
        "mn", "Mongolia",
        "ms", "Montserrat",
        "ma", "Morocco",
        "mz", "Mozambique",
        "na", "Namibia",
        "nr", "Nauru",
        "np", "Nepal",
        "nl", "Netherlands",
        "an", "Netherlands Antilles",
        "nc", "New Caledonia",
        "nz", "New Zealand",
        "ni", "Nicaragua",
        "ne", "Niger",
        "ng", "Nigeria",
        "nu", "Niue",
        "nf", "Norfolk Island",
        "mp", "Northern Mariana Islands",
        "no", "Norway",
        "nd", "Not Determined",
        "om", "Oman",
        "pk", "Pakistan",
        "pw", "Palau",
        "pa", "Panama",
        "pg", "Papua New Guinea",
        "py", "Paraguay",
        "pe", "Peru",
        "ph", "Philippines",
        "pn", "Pitcairn Island",
        "pl", "Poland",
        "pt", "Portugal",
        "pr", "Puerto Rico",
        "qa", "Qatar",
        "re", "Reunion Island",
        "ro", "Romania",
        "ru", "Russian Federation",
        "rw", "Rwanda",
        "kn", "Saint Kitts and Nevis",
        "lc", "Saint Lucia",
        "vc", "Saint Vincent and the Grenadines",
        "sm", "San Marino",
        "st", "Sao Tome and Principe",
        "sa", "Saudi Arabia",
        "sn", "Senegal",
        "sc", "Seychelles",
        "sl", "Sierra Leone",
        "sg", "Singapore",
        "sk", "Slovak Republic",
        "si", "Slovenia",
        "sb", "Solomon Islands",
        "so", "Somalia",
        "za", "South Africa",
        "gs", "South Georgia and the South Sandwich Islands",
        "es", "Spain",
        "lk", "Sri Lanka",
        "pm", "St Pierre and Miquelon",
        "sh", "St. Helena",
        "sr", "Suriname",
        "sj", "Svalbard and Jan Mayen Islands",
        "sz", "Swaziland",
        "se", "Sweden",
        "ch", "Switzerland",
        "tw", "Taiwan",
        "tj", "Tajikistan",
        "tz", "Tanzania",
        "th", "Thailand",
        "tg", "Togo",
        "tk", "Tokelau",
        "to", "Tonga",
        "tt", "Trinidad and Tobago",
        "tn", "Tunisia",
        "tr", "Turkey",
        "tm", "Turkmenistan",
        "tc", "Turks and Caicos Islands",
        "tv", "Tuvalu",
        "ug", "Uganda",
        "ua", "Ukraine",
        "ae", "United Arab Emirates",
        "gb", "United Kingdom",
        "us", "United States",
        "uy", "Uruguay",
        "um", "US Minor Outlying Islands",
        "uz", "Uzbekistan",
        "vu", "Vanuatu",
        "ve", "Venezuela",
        "vn", "Vietnam",
        "vg", "Virgin Island (British)",
        "vi", "Virgin Islands (USA)",
        "wf", "Wallis And Futuna Islands",
        "eh", "Western Sahara",
        "ws", "Western Samoa",
        "ye", "Yemen",
        "yu", "Yugoslavia",
        "zm", "Zambia",
        "zw", "Zimbabwe",
    };

    public void prefixChanged(ValueChangeEvent event) {
        filteredCountries = buildFilteredCountries((String)event.getNewValue());
    }

    private List<SelectItem> filteredCountries;

    private List<SelectItem> buildFilteredCountries(String prefix) {
        List<SelectItem> result = new ArrayList<SelectItem>();
        if (prefix != null) prefix = prefix.toLowerCase();
        for (int i = 0; i < ALL_COUNTRIES.length; i += 2) {
            String abbr = ALL_COUNTRIES[i];
            String country = ALL_COUNTRIES[i+1];
            if (prefix == null || prefix.length() == 0 || country.toLowerCase().startsWith(prefix)) {
                result.add(new SelectItem(abbr, country));
            }
        }
        return result;
    }

    public List<SelectItem> getFilteredCountries() {
        if (filteredCountries == null)
            filteredCountries = buildFilteredCountries(this.prefix);
        return filteredCountries;
    }

    private String country;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    private String[] countries;

    public String[] getCountries() {
        return countries;
    }

    public void setCountries(String[] countries) {
        this.countries = countries;
    }
}
