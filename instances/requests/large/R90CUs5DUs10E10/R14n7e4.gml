graph [
  node [
    id 0
    label "0"
    type 2
    prc 3
  ]
  node [
    id 1
    label "1"
    type 2
    prc 3
  ]
  node [
    id 2
    label "2"
    type 2
    prc 3
  ]
  node [
    id 3
    label "3"
    type 1
    prc 1
    ant 3
    prb 2
    x 54
    y 79
  ]
  node [
    id 4
    label "4"
    type 1
    prc 1
    ant 8
    prb 4
    x 67
    y 29
  ]
  node [
    id 5
    label "5"
    type 1
    prc 2
    ant 5
    prb 6
    x 105
    y 32
  ]
  node [
    id 6
    label "6"
    type 1
    prc 5
    ant 3
    prb 3
    x 107
    y 34
  ]
  edge [
    source 0
    target 6
    bandwith 175
    delay 418
  ]
  edge [
    source 1
    target 5
    bandwith 968
    delay 494
  ]
  edge [
    source 2
    target 4
    bandwith 263
    delay 248
  ]
  edge [
    source 2
    target 3
    bandwith 355
    delay 384
  ]
]
