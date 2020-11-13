// Exports the Cannith Crafting configuration data.
//
// Paste the following into the broser console while at
// http://ccplanner.byethost14.com/main.html
//
// If successful, the system clipboard will contain a new JSON file.

window.CannithCraftingScraper = (function() {
    function getMasterObject() {
        return {
            scaling: getScalingObject(),
            options: getOptionsObject()
        };
    }

    function getScalingObject() {
        const ret = {};

        for(let i = 1; i <= scale_info.length; i++) {
            const stat = getStatScalingObject(i);
            
            if(shouldExclude(stat.name)) continue;
            
            
            
            ret[stat.name] = stat.mags;
        }
        
        return ret;
    }
    
    const EXCLUDED_STATS = ["None"]; // Exclude the stat named "None", as opposed to not excluding stats.
    function shouldExclude(statName) {
        return EXCLUDED_STATS.includes(statName);
    }
    
    function getStatScalingObject(ench_id) {
        let name = getStatName(ench_id);
        let mags = null;
        
        if(! isNonScalingStat(name))
            mags = getMagnitudesArray(ench_id);
        name = cleanupStatName(name);
        
        return {
            name: name,
            mags: mags
        };
    }

    function getStatName(ench_id) {
        const i = getScaleInfoIndex(ench_id);
        if(i == null) return null;
        
        return scale_info[i][1];
    }

    function getMagnitudesArray(ench_id) {
        const ret = [0]; // Put zero in slot zero, to make referencing easy.
        
        for(let minLevel = 1; minLevel <= 29; minLevel++)
            ret[minLevel] = getStatMagnitude(ench_id, minLevel);
        
        // Put power level 34 in the ML30 slot.
        ret[30] = getStatMagnitude(ench_id, 34);
        
        return ret;
    }
    
    const NON_SCALING_PREFIX = ["Efficient Metamagic", "Non-scaling"];
    function isNonScalingStat(name) {
        const prefix = name.split(": ")[0];
        
        return NON_SCALING_PREFIX.includes(prefix);
    }
    
    const RETAINED_PREFIXES = ["Efficient Metamagic"];
    function cleanupStatName(name) {
        const prefix = name.split(": ")[0];
        
        if(RETAINED_PREFIXES.includes(prefix)) return name;
        
        const index = name.indexOf(": ");
        if(index == -1) return name;
        
        return name.substring(index +2);
    }
    
    function getStatMagnitude(ench_id, powerLevel) {
        const i = getScaleInfoIndex(ench_id);
        if(i == null) return null;
        
        return power_info[scale_info[i][6]][powerLevel -1];
    }
    
    // Searches for an ench_id in the byethost array, scale_info.
    function getScaleInfoIndex(ench_id) {
        for (var i = 0; i < scale_info.length; i++)
            if (scale_info[i][0] == ench_id)
                return i;
        
        return null;
    }

    const ITEM_TYPES = {
        armor:      {id: 1,  augments: "green", slots: ["armor"]},
        weapon:     {id: 2,  augments: "red",   slots: ["main hand", "off hand"]},
        collar:     {id: 2,  augments: "red",   slots: ["pet collar"]},
        shield:     {id: 3,  augments: "red",   slots: ["off hand"]},
        belt:       {id: 4,  augments: "green", slots: ["waist"]},
        boots:      {id: 5,  augments: "green", slots: ["feet"]},
        cloak:      {id: 6,  augments: "green", slots: ["back"]},
        gloves:     {id: 7,  augments: "green", slots: ["hand"]},
        helm:       {id: 8,  augments: "green", slots: ["head"]},
        bracers:    {id: 9,  augments: "green", slots: ["wrist"]},
        goggles:    {id: 10, augments: "green", slots: ["eye"]},
        necklace:   {id: 11, augments: "green", slots: ["neck"]},
        ring:       {id: 12, augments: "green", slots: ["finger"]},
        trinket:    {id: 13, augments: "none",  slots: ["trinket"]},
        "rune arm": {id: 14, augments: "red",   slots: ["off hand"]},
        // Skip 15. Collars are type 15, but use the same mods list as Weapon.
        orb:        {id: 16, augments: "red",   slots: ["off hand"]}
    };
    function getOptionsObject() {
        const ret = {};
        
        for(const [itemTypeName, itemTypeData] of Object.entries(ITEM_TYPES)) {
            ret[itemTypeName] = {
                slots: itemTypeData.slots,
                options: enumerateOptions(itemTypeData.id, itemTypeData.augments)
            };
        }
        
        return ret;
    }

    const AFFIX_SLOTS = ["prefix", "suffix", "extra"];
    const AUGMENT_OPTIONS = {
        none: {},
        red: {
            primary: ["empty red augment slot"],
            secondary: ["empty orange augment slot", "empty purple augment slot"]
        },
        green: {
            primary: ["empty yellow augment slot", "empty blue augment slot"],
            secondary: ["empty green augment slot"]
        }
    };
    function enumerateOptions(itemTypeId, augmentsCategory) {
        const ret = {};

        for(let slot = 0; slot < AFFIX_SLOTS.length; slot++)
            ret[AFFIX_SLOTS[slot]] = getAffixOptionsTypeSlot(itemTypeId, slot);
        
        for(const [affixSlotName, optionsArray] of Object.entries(AUGMENT_OPTIONS[augmentsCategory]))
            ret[affixSlotName] = optionsArray;
        
        return ret;
    }
    
    function getAffixOptionsTypeSlot(itemTypeId, affixSlotId) {
        const ret = [];

        for (var i = 0; i < scale_info.length - 1; i++)
            if (scale_info[i][5][affixSlotId].indexOf(itemTypeId) > -1)
                ret.push(cleanupStatName(scale_info[i][1]));
        
        return ret;
    }

    return { getMasterObject: getMasterObject };
})();

(function runScraper() {
    const masterObject = CannithCraftingScraper.getMasterObject();
    const json = JSON.stringify(masterObject).toLowerCase();

    console.log(json);
    console.log("Copied JSON to clipboard!");

    copy(json); // to clipboard
})();
