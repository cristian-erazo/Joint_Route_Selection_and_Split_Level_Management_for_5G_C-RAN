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
    prc 3
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
    prc 2
  ]
  node [
    id 4
    label "4"
    type 1
    prc 5
    ant 7
    prb 5
    x 24
    y 79
  ]
  node [
    id 5
    label "5"
    type 1
    prc 5
    ant 4
    prb 3
    x 101
    y 110
  ]
  node [
    id 6
    label "6"
    type 1
    prc 1
    ant 7
    prb 6
    x 104
    y 77
  ]
  node [
    id 7
    label "7"
    type 1
    prc 2
    ant 10
    prb 6
    x 109
    y 81
  ]
  node [
    id 8
    label "8"
    type 1
    prc 1
    ant 8
    prb 5
    x 50
    y 66
  ]
  edge [
    source 0
    target 8
    bandwith 808
    delay 366
  ]
  edge [
    source 1
    target 5
    bandwith 746
    delay 343
  ]
  edge [
    source 2
    target 4
    bandwith 146
    delay 362
  ]
  edge [
    source 3
    target 7
    bandwith 977
    delay 281
  ]
  edge [
    source 3
    target 6
    bandwith 293
    delay 331
  ]
]
