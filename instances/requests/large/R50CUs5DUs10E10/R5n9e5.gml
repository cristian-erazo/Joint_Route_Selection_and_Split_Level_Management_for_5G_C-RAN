graph [
  node [
    id 0
    label "0"
    type 2
    prc 2
  ]
  node [
    id 1
    label "1"
    type 2
    prc 2
  ]
  node [
    id 2
    label "2"
    type 2
    prc 4
  ]
  node [
    id 3
    label "3"
    type 2
    prc 4
  ]
  node [
    id 4
    label "4"
    type 1
    prc 4
    ant 2
    prb 3
    x 33
    y 94
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 6
    prb 5
    x 61
    y 86
  ]
  node [
    id 6
    label "6"
    type 1
    prc 4
    ant 7
    prb 5
    x 87
    y 104
  ]
  node [
    id 7
    label "7"
    type 1
    prc 4
    ant 9
    prb 4
    x 37
    y 76
  ]
  node [
    id 8
    label "8"
    type 1
    prc 3
    ant 3
    prb 6
    x 72
    y 16
  ]
  edge [
    source 0
    target 8
    bandwith 642
    delay 214
  ]
  edge [
    source 1
    target 5
    bandwith 194
    delay 469
  ]
  edge [
    source 2
    target 6
    bandwith 167
    delay 440
  ]
  edge [
    source 3
    target 4
    bandwith 393
    delay 411
  ]
  edge [
    source 3
    target 7
    bandwith 993
    delay 317
  ]
]
