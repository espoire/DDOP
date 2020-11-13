// Exports the Cannith Crafting configuration data.
//
// Paste the following into the broser console while at
// http://ccplanner.byethost14.com/main.html
//
// If successful, the system clipboard will contain a new JSON file.

function getStatDetails(ench_id, level) {
    const min_lvl = level - 1; // array indexed from zero
    let arr_id = -1;

    for (var i = 0; i < scale_info.length; i++) {
        if (scale_info[i][0] == ench_id) {
            arr_id = i;
            break;
        }
    }
    
    if(arr_id == -1) return null;

    return {
        categoryType: scale_info[arr_id][1],
        magnitude: power_info[scale_info[arr_id][6]][min_lvl]
    };
}

function getStatName(ench_id) {
    return getStatDetails(ench_id).categoryType;
}

function getMagnitudesArray(ench_id) {
    const ret = [0];
    
    for(let i = 1; i <= 29; i++)
        ret.push(getStatDetails(ench_id, i).magnitude);
    ret.push(getStatDetails(ench_id, 34).magnitude);
    
    return ret;
}

function getStatObject(ench_id) {
    const name = getStatName(ench_id);
    const mags = getMagnitudesArray(ench_id);
    
    return {
        name: name,
        mags: mags
    };
}

const EXCLUDED = ["None"]; // Exclude the stat named "None", as opposed to not excluding stats.
const NON_SCALING_PREFIX = [
    "Efficient Metamagic",
    "Non-scaling"
];
function getStatScalingObject() {
    const ret = {};

    for(let i = 1; i <= scale_info.length; i++) {
        const stat = getStatObject(i);
        if(EXCLUDED.includes(stat.name)) continue;
        
        const prefix = stat.name.split(": ")[0];
        if(NON_SCALING_PREFIX.includes(prefix))
            stat.mags = null;
        
        stat.name = cleanupStatName(stat.name);
        
        
        ret[stat.name] = stat.mags;
    }
    
    return ret;
}

const RETAIN_PREFIX = ["Efficient Metamagic"];
function cleanupStatName(name) {
    const prefix = name.split(": ")[0];
    
    if(RETAIN_PREFIX.includes(prefix)) return name;
    
    if(name.split(": ").length != 2) throw new Error(name);
    return name.split(": ")[1];
}

const AFFIX_SLOTS = ["prefix", "suffix", "extra"];
function getAffixOptionsTypeSlot(itemTypeId, affixSlotId) {
    const ret = [];

    for (var i = 0; i < scale_info.length - 1; i++)
        if (scale_info[i][5][affixSlotId].indexOf(itemTypeId) > -1)
            ret.push(cleanupStatName(scale_info[i][1]));
    
    return ret;
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
}
function getOptionsObject() {
    const ret = {};
    
    for(const [itemTypeName, itemTypeData] of Object.entries(ITEM_TYPES)) {
        ret[itemTypeName] = {
            slots: itemTypeData.slots,
            options: enumerateOptions(itemTypeData.id, itemTypeData.augments)
        }
    }
    
    return ret;
}

const AUGMENT_OPTIONS = {
    red: {
        augment1: ["empty orange augment slot", "empty purple augment slot"],
        augment2: ["empty red augment slot"]
    },
    green: {
        augment1: ["empty green augment slot"],
        augment2: ["empty yellow augment slot", "empty blue augment slot"]
    },
    none: {}
}
function enumerateOptions(itemTypeId, augmentsCategory) {
    const ret = {};

    for(let i = 0; i < AFFIX_SLOTS.length; i++) {
        ret[AFFIX_SLOTS[i]] = getAffixOptionsTypeSlot(itemTypeId, i);
    }
    
    for(const [affixSlotName, optionsArray] of Object.entries(AUGMENT_OPTIONS[augmentsCategory])) {
        ret[affixSlotName] = optionsArray;
    }
    
    return ret;
}

function getMasterObject() {
    return {
        scaling: getStatScalingObject(),
        options: getOptionsObject()
    }
}

let masterObject = getMasterObject();
copy(JSON.stringify(masterObject).toLowerCase());



































