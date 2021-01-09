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
    prc 5
  ]
  node [
    id 2
    label "2"
    type 1
    prc 2
    ant 4
    prb 2
    x 45
    y 40
  ]
  node [
    id 3
    label "3"
    type 1
    prc 3
    ant 2
    prb 4
    x 28
    y 19
  ]
  node [
    id 4
    label "4"
    type 1
    prc 3
    ant 4
    prb 2
    x 94
    y 35
  ]
  node [
    id 5
    label "5"
    type 1
    prc 3
    ant 3
    prb 4
    x 27
    y 54
  ]
  edge [
    source 0
    target 2
    bandwith 440
    delay 461
  ]
  edge [
    source 0
    target 3
    bandwith 357
    delay 290
  ]
  edge [
    source 1
    target 4
    bandwith 803
    delay 469
  ]
  edge [
    source 1
    target 5
    bandwith 880
    delay 151
  ]
]
